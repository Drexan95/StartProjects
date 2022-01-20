package main;

import Database.DBConnection;
import UrlService.HTMLDataFilter;
import UrlService.URLCollector;
import lombok.Getter;
import main.model.*;
import main.repository.*;
import org.hibernate.StaleObjectStateException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;

@Component
public class IndexingCommands {
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private SearchIndexRepository searchIndexRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ManagementCommands managementCommands;
    private Statement statement;
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Value("${sites.url}")
    private String[] urls;
    @Value("${sites.name}")
    private String[] names;
    @Autowired
    URLCollector collector;
    @Getter
    private final List<URLCollector> tasks = new ArrayList<>();
    @Getter
    private final List<ForkJoinPool> pools = new ArrayList<>();
    @Getter
    private final List<Thread> threads = new ArrayList<>();
    private final List<Site> sites = new ArrayList<>();

    protected void start() throws SQLException, IOException, ParseException, JSONException, InterruptedException {
        Statement statement = DBConnection.getConnection().createStatement();
        statement.executeUpdate("DELETE FROM site");
        statement.executeUpdate("DELETE FROM page");
        statement.executeUpdate("DELETE FROM field");
        statement.executeUpdate("DELETE FROM lemma");
        statement.executeUpdate("DELETE FROM search_index");
        statement.executeUpdate("ALTER TABLE search_index AUTO_INCREMENT=1");

        for (int i = 0; i < urls.length; i++) {
            Site site = new Site(urls[i]);
            site.setId(i + 1);
            site.setName(names[i]);
            site.setStatusTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            site.setStatus(StatusType.INDEXING);
            site.setLastError(null);
            sites.add(site);
//                siteRepository.save(site); StableObject exception
            Thread.sleep(1000);
            URLCollector collector = new URLCollector();
            autowireCapableBeanFactory.autowireBean(collector);
            collector.createCollector(site);
            tasks.add(collector);
        }
        siteRepository.saveAll(sites); //no exception

    }
    protected void indexing(List<URLCollector> collectors)  {
        Long start = System.currentTimeMillis();
        collectors.forEach(collector -> {
            try {
                threads.add(new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            ForkJoinPool pool = new ForkJoinPool();
                            pools.add(pool);
                            pool.execute(collector);
                            int pageCount = collector.join();
                            collector.collectFrequency();
                            collector.getSite().setStatus(StatusType.INDEXED);
                            siteRepository.save(collector.getSite());
                            System.out.println("Сайт " + collector.getSite().getName() + " проиндексирован,кол-во ссылок - "+pageCount);
                            System.out.println("Время выполнения: " + (System.currentTimeMillis() - start));
                        }
                        catch (InterruptedException | SQLException | CancellationException ex) {
                            ex.printStackTrace();
                            collector.getSite().setLastError(ex.getMessage());
                            collector.getSite().setStatus(StatusType.FAILED);
                            siteRepository.save(collector.getSite());
                        }
                    }

                });

            }
            catch (StaleObjectStateException exception)
            {
                Optional<Site> siteOptional = siteRepository.findById(collector.getSite().getId());
                if (siteOptional.isPresent()) {
                    siteOptional.get().setLastError(exception.getMessage());
                    siteRepository.save(siteOptional.get());
                }
                exception.printStackTrace();
            }
        });
        threads.forEach(Thread::start);
        pools.forEach(ForkJoinPool::shutdown);
        pools.clear();
    }
    /**
     * Method start reindexing the given page
     * Url must be correspond the sites given in application.yml
     * @param url
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws JSONException
     */
    @Transactional
    public ResponseEntity<String> addPage(@RequestParam(name = "url") String url) throws SQLException, IOException, JSONException
    {
        Iterable<Site> sites = siteRepository.findAll();
        Page page = new Page(url);
        Site pageSite = new Site();
        JSONObject response = new JSONObject();
        String pageUrl = page.getPath();
        System.out.println(page.getPath());
        page.setPath("");
        for(Site site : sites){
            if (pageUrl.startsWith(site.getUrl())) {
                page.setSiteid(site.getId());
                page.setSite(site.getUrl());
                page.setSiteName(site.getName());
                pageSite.setId(site.getId());
                pageSite.setUrl(site.getUrl());
                pageSite.setStatus(site.getStatus());
                page.setPath(pageUrl);
            }
        }

        //==========================================================================================================================================
        if(page.getPath().equals("")){
            try {

                response.put("result", false);
                response.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
                return new ResponseEntity<>(response.toString(),HttpStatus.BAD_REQUEST);
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        else if(!pageSite.getStatus().equals(StatusType.INDEXED)){
            try {

                response.put("result", false);
                response.put("error", "Индексация еще не завершена");
                return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
            } catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        if(page.getPath().equals(pageSite.getUrl())){
            page.setPath(page.getPath()+"/");
        }
        //==========================================================================================================================================
        Iterable<Page> pageIterable = pageRepository.findAll();
        Set<String> pages = Collections.synchronizedSet(new HashSet<>());
        URLCollector collector = new URLCollector();
        autowireCapableBeanFactory.autowireBean(collector);
        setDataToCollector(collector,page);


        pageIterable.forEach(p -> {
            if (p.getSiteid().equals(page.getSiteid()) && p.getPath().equals(page.getPath().split(pageSite.getUrl())[1])) {
                searchIndexRepository.deleteSearchIndexes(p.getId());
                pageRepository.deleteById(p.getId());
            }
            else if(p.getSiteid().equals(page.getSiteid())) {
                pages.add(pageSite.getUrl()+ p.getPath());

            }
        });
        collector.setVisitedInternalLink(pages);
        collector.createCollector(pageSite);
        collector.setPath(page.getPath());
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(collector);
        collector.collectFrequency();
        pool.shutdown();
        response.put("result",true);
        return new ResponseEntity<>(response.toString(),HttpStatus.OK);
    }

    public void setDataToCollector(URLCollector collector,Page page) throws SQLException {
        statement = DBConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) as maxId from page");
        while (resultSet.next()){
            page.setId(resultSet.getInt("maxId")+1);
            collector.getPageId().set(page.getId()-1);
        }
        Iterable<Lemma> lemmaIterable = lemmaRepository.findAll();
        collector.getLemmaId().set((int) lemmaRepository.count());
        collector.getFieldId().set((int) fieldRepository.count());
        lemmaIterable.forEach(lemma -> {
            collector.getLemmaIds().put(lemma.getName(), lemma.getId());
            collector.getFrequency().put(lemma.getName(), lemma.getFrequency());
        });
    }



}
