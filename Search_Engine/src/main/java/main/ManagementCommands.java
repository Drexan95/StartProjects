package main;


import UrlService.URLCollector;
import main.model.*;
import main.repository.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ManagementCommands {
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
    private Statistics statistics;
    @Autowired
    private  IndexingCommands indexingCommands;
    @Autowired
    PageResults pageResults;
    private Statement statement;
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;


    /**
     * Method start full indexing sites given in application.yml
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws ParseException
     * @throws JSONException
     */
   public ResponseEntity<String> startIndexing() throws SQLException, IOException, ParseException, JSONException {
      indexingCommands.start();
        Iterable<Site> sites = siteRepository.findAll();
        AtomicBoolean isIndexing = new AtomicBoolean(false);
        sites.forEach(site -> {
            if(site.getStatus().equals(StatusType.INDEXING)){
                isIndexing.set(true);
            }
        });

        if (!isIndexing.get()) {
            System.out.println("Indexing");
           indexingCommands.indexing(indexingCommands.getTasks());
            sites.iterator().forEachRemaining(s->s.setStatus(StatusType.INDEXING));
            siteRepository.saveAll(sites);
        }

        return new ResponseEntity<>(jsonStartIndexing(isIndexing.get()).toString(), HttpStatus.OK);
    }

    /**
     * Method abort indexing
     * @return
     * @throws JSONException
     */
    public ResponseEntity<String> stopIndexing() throws JSONException {
        boolean isIndexing = indexingCommands.getPools().isEmpty();
        if(!isIndexing){
          indexingCommands.getPools().forEach(ForkJoinPool::shutdownNow);
           indexingCommands.getTasks().clear();
           indexingCommands.getPools().clear();
        }
        return new ResponseEntity<>(jsonStopIndexing(isIndexing).toString(),HttpStatus.OK);
    }


    public ResponseEntity<?> getStatistics() {
        statistics.setSites(siteRepository.count());
        statistics.setPages(pageRepository.count());
        statistics.setLemmas(lemmaRepository.count());
//        boolean isIndexing = pools.size() > 0;
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
    public static JSONObject jsonStartIndexing(boolean status) throws JSONException
    {
        JSONObject response = new JSONObject();
        if (!status) {
            response.put("result", "true");
            return response;
        } else
            response.put("result", "false");
        response.put("error", "индексация уже запущена");
        return response;

    }
    public static JSONObject jsonStopIndexing(boolean status) throws JSONException
    {
        JSONObject response = new JSONObject();
        if(!status){
            response.put("result","true");
            return response;
        }
        else response.put("result","false");
        response.put("error","индексация не запущена");
        return response;
    }

}