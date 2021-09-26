package main;

import Database.DBConnection;
import UrlService.Helper;
import UrlService.URLCollector;
import main.model.*;
import org.hibernate.StaleObjectStateException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ServiceHelper {
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
    URLCollector collector;
    @Autowired
    Statistics statistics;
    @Autowired
    PageResults pageResults;
    private Statement statement;
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
    @Value("${sites.url}")
    private String[] urls;
    @Value("${sites.name}")
    private String[] names;
    private final List<URLCollector> tasks = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final List<ForkJoinPool> pools = new ArrayList<>();
    List<Thread> threads = new ArrayList<>();

    public ServiceHelper() {

    }
    protected void start() throws SQLException, IOException, ParseException, JSONException {
        Statement statement = DBConnection.getConnection().createStatement();
        statement.executeUpdate("DELETE FROM site");
        statement.executeUpdate("DELETE FROM page");
        statement.executeUpdate("DELETE FROM field");
        statement.executeUpdate("DELETE FROM lemma");
        statement.executeUpdate("DELETE FROM search_index");
        statement.executeUpdate("ALTER TABLE search_index AUTO_INCREMENT=1");
        for (int i = 0; i < urls.length; i++) {
            Site site = new Site((Helper.slashAtEnd(urls[i])));
            site.setId(i + 1);
            site.setName(names[i]);
            site.setStatusTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            site.setStatus(StatusType.failed);
            site.setLastError(null);
            siteRepository.save(site);
            URLCollector collector = new URLCollector();
            autowireCapableBeanFactory.autowireBean(collector);
            collector.createCollector(site);
            tasks.add(collector);
        }
    }

    /**
     * Method start full indexing sites given in application.yml
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws ParseException
     * @throws JSONException
     */
    protected ResponseEntity<String> startIndexing() throws SQLException, IOException, ParseException, JSONException {
        start();
        Iterable<Site> sites = siteRepository.findAll();
        AtomicBoolean isIndexing = new AtomicBoolean(false);
        sites.forEach(site -> {
            if(site.getStatus().equals(StatusType.indexing)){
                isIndexing.set(true);
            }
        });

        if (!isIndexing.get()) {
            System.out.println("Indexing");
            indexing(tasks);
            sites.iterator().forEachRemaining(s->s.setStatus(StatusType.indexing));
            siteRepository.saveAll(sites);
        }

        return new ResponseEntity<>(Helper.jsonStartIndexing(isIndexing.get()).toString(), HttpStatus.OK);
    }

    /**
     * Method abort indexing
     * @return
     * @throws JSONException
     */
    protected ResponseEntity<String> stopIndexing() throws JSONException {
        boolean isIndexing = pools.isEmpty();
        if(!isIndexing){
            pools.forEach(ForkJoinPool::shutdownNow);
            tasks.clear();
            pools.clear();

        }
        return new ResponseEntity<>(Helper.jsonStopIndexing(isIndexing).toString(),HttpStatus.OK);

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
                            collector.getSite().setStatus(StatusType.indexed);
                            siteRepository.save(collector.getSite());
                            System.out.println("Сайт " + collector.getSite().getName() + " проиндексирован,кол-во ссылок - "+pageCount);
                            System.out.println("Время выполнения: " + (System.currentTimeMillis() - start));


                        }
                        catch (InterruptedException | SQLException | CancellationException ex) {
                            ex.printStackTrace();
                            collector.getSite().setLastError(ex.getMessage());
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
//        for (URLCollector collector : tasks) {
//            System.out.println(collector.getSite().getName() + " " + collector.getSite().getId() + " " + collector.getSite().getUrl());
//        }
//        System.out.println(tasks.size());
        pools.forEach(ForkJoinPool::shutdown);
        pools.clear();
    }

    protected ResponseEntity<?> getStatistics() {
        statistics.setSites(siteRepository.count());
        statistics.setPages(pageRepository.count());
        statistics.setLemmas(lemmaRepository.count());
        boolean isIndexing = pools.size() > 0;
        statistics.setIndexing(isIndexing);
        Iterable<Site> sites = siteRepository.findAll();
        sites.iterator().forEachRemaining(site -> {
            try {
                statistics.getDetailedData(site);

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
        if (siteRepository.count() > 0) {
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        }
        else  return  new ResponseEntity<>("Ни один сайт не проиндексирован", HttpStatus.BAD_REQUEST);

    }
    //////////////////////////////////////////////////////////////PAGE//////////////////////////////////////////////////////////

    /**
     * Method return pages matching search query
     * @param query words to search
     * @param site search in particular site
     * @param limit page results
     * @param offset skip first n results
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws JSONException
     */
    protected ResponseEntity<?> search(@RequestParam(name = "query") String query, @Nullable String site, @Nullable Integer limit, @Nullable Integer offset) throws SQLException, IOException, JSONException
    {

        SearchRequest request = new SearchRequestBuilder().setText(query).setSiteUrl(site).setLimit(limit).setOffset(offset).build();
        List<Page> results = pageResults.getResults(request);
//        results.forEach(System.out::println);
        if(query.equals("")){
            JSONObject response = new JSONObject();
            response.put("result","false");
            response.put("error","задан пустой поисковой запрос");
            return new ResponseEntity<>( response.toString(),HttpStatus.BAD_REQUEST);
        }
        if(results.size()>0){
            return new ResponseEntity<>(results,HttpStatus.OK);
        }
        else {
            JSONObject response = new JSONObject();
            response.put("result","false");
            response.put("error","Совпадения не найдены");
            return new ResponseEntity<>(response.toString(),HttpStatus.OK);
        }
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
protected ResponseEntity<String> addPage(@RequestParam(name = "url") String url) throws SQLException, IOException, JSONException
    {
        Iterable<Site> sites = siteRepository.findAll();
        Page page = new Page(Helper.slashAtEnd(url));
        Site pageSite = new Site();
        JSONObject response = new JSONObject();

        String pageUrl = page.getPath();
        System.out.println(page.getPath());
        sites.forEach(site -> {
            if (pageUrl.startsWith(site.getUrl())) {

                page.setSiteId(site.getId());
                pageSite.setId(site.getId());
                pageSite.setUrl(site.getUrl());
                pageSite.setStatus(site.getStatus());
                if(pageUrl.equals(site.getUrl())){
                    page.setPath(site.getUrl());
                }
                else   {
                    page.setPath(pageUrl.split(site.getUrl())[1]);
                }
            }
            else page.setPath("");

        });

        if(pageSite.getStatus().equals(StatusType.indexing)){
            try {
                response.put("result", "false");
                response.put("error", "Индексация еще не завершена");
                return new ResponseEntity<>(response.toString(),HttpStatus.BAD_REQUEST);
            } catch (JSONException ex){
                ex.printStackTrace();
            }
        }

        if(page.getPath().equals("")){
            try {
                response.put("result", "false");
                response.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
                return new ResponseEntity<>(response.toString(),HttpStatus.BAD_REQUEST);
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        Iterable<Page> pageIterable = pageRepository.findAll();
        Set<String> pages = Collections.synchronizedSet(new HashSet<>());
        statement = DBConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) as maxId from page");
        while (resultSet.next()){
            page.setId(resultSet.getInt("maxId")+1);
            collector.getPageId().set(page.getId()-1);
        }

        pageIterable.forEach(p -> {
            if (p.equals(page)) {

                searchIndexRepository.deleteSearchIndexes(p.getId());
                pageRepository.deleteById(p.getId());
            }
            else {
                pages.add(pageSite.getUrl()+Helper.slashAtEnd(p.getPath()));

            }
        });


        URLCollector collector = new URLCollector();
        autowireCapableBeanFactory.autowireBean(collector);
        collector.setVisitedInternalLink(pages);
        collector.createCollector(pageSite);
        collector.setPath(pageUrl);
        Iterable<Lemma> lemmaIterable = lemmaRepository.findAll();
        collector.getLemmaId().set((int) lemmaRepository.count());
        collector.getFieldId().set((int) fieldRepository.count());
        lemmaIterable.forEach(lemma -> {
            collector.getLemmaIds().put(lemma.getName(), lemma.getId());
            collector.getFrequency().put(lemma.getName(), lemma.getFrequency());
        });
        ForkJoinPool pool = new ForkJoinPool();
        System.out.println(collector.getFrequency().size());
        pool.execute(collector);
        System.out.println(collector.getFrequency().size());
        int result = collector.join();
        collector.collectFrequency();
        pool.shutdown();
        response.put("result","true");
        return new ResponseEntity<>(response.toString(),HttpStatus.OK);
    }
}
