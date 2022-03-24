package main;

import Database.DBConnection;
import Lemmatizer.Lem;
import UrlService.HTMLDataFilter;
import lombok.Getter;
import main.model.*;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PageResults {


    private Statement statement;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Getter
    private long maxResults;



    private List<Lemma> getLemms(SearchRequest request) throws IOException {
        try {
            Lem.createMorph();
            statement = DBConnection.getConnection().createStatement();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
        Map<String, Integer> lemms = new ConcurrentHashMap<>(Lem.searchForLem(request.getText()));
        List<Lemma> frequencyLemms = new ArrayList<>();//List of lemmas to find

        /**
         * Find lemmas in database
         */
        lemms.keySet().forEach(word -> {
            try {
                ResultSet resultSet = statement.executeQuery("SELECT id FROM lemma WHERE lemma.lemma = " + '\'' + word + '\'');
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    Optional<Lemma> lemma = lemmaRepository.findById(id);
                    lemma.ifPresent(frequencyLemms::add);
                }
            } catch (SQLException | NullPointerException exception) {
                exception.printStackTrace();
                System.out.println("Совпадения  не найдены");
            }
        });
        Collections.sort(frequencyLemms);

        return frequencyLemms;
    }

    /**
     * Find pages where lemma occur
     * @param request
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws NullPointerException
     */
    @Transactional
    private List<Lemma> getListOfUrls(SearchRequest request) throws IOException, SQLException, NullPointerException {
        List<Lemma> frequencyLemms = getLemms(request);
        AtomicLong siteId = new AtomicLong();
        int pageCount = 0;
        if (!request.getSiteUrl().equals("")) {
            String siteUrl = request.getSiteUrl();
            Iterable<Site> siteIterable = siteRepository.findAll();
            for (Site site : siteIterable) {
                if (site.getUrl().equals(siteUrl)) {
                    siteId.set(site.getId());

                }
            }
            ResultSet result = statement.executeQuery("SELECT COUNT(id) as page_count  FROM page where site_id= "+siteId.get());
            while (result.next()) {
                pageCount = result.getInt("page_count");
            }


        }

        //=====================================================================================================================
        else {
            ResultSet result = statement.executeQuery("SELECT COUNT(id) as page_count  FROM page");
            while (result.next()) {
                pageCount = result.getInt("page_count");
            }
        }

/**
 * Find pages where lemma appears
 *
 */
        String query = "SELECT page.id FROM search_engine.page  JOIN search_index ON page.id = search_index.page_id JOIN lemma ON lemma.id = search_index.lemma_id " +
                "WHERE lemma_id = ?";
        if (siteId.get() != 0) {
            query = query + " AND page.site_id =" + siteId.get();
        }
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);

        frequencyLemms.forEach(lemma -> {
            try {
                preparedStatement.setInt(1, lemma.getId());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("page.id");
                    Optional<Page> page = pageRepository.findById(id);
                    if (page.isPresent() && !page.get().getLemms().contains(lemma.getName())) {
                        lemma.getUrls().add(page.get());
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        int finalPageCount = pageCount;  //Don't consider lemma if it appears in more than 40% of the pages
        try {
            frequencyLemms.removeIf(lemma ->lemma.getUrls().size()!=0 && 100 / (finalPageCount / lemma.getUrls().size()) > 40 && frequencyLemms.size() > 1);
            if (frequencyLemms.size() == 1) {
                final int RESULTS_TO_SHOW = 20;
                frequencyLemms.get(0).setUrls(frequencyLemms.get(0).getUrls().stream().limit(RESULTS_TO_SHOW).collect(Collectors.toList()));
            }
            frequencyLemms.stream().sorted().skip(1)
                    .forEach(lemma -> lemma.getUrls().subList(lemma.getUrls().size() - (int) ((lemma.getUrls().size() * 10L) / 100), lemma.getUrls().size()).clear()
                    );//Decrease number of pages for each lemma by 10%

        } catch (ArithmeticException ae) {
            ae.printStackTrace();
        }


        return frequencyLemms;
    }

//=====================================================================================================================================================================

    /**
     * Calculate absolute relevancy
     * method calculates the relevance of pages upon request
     *
     * @param lemms
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @Transactional
    private List<Page> calculateRelevancy(List<Lemma> lemms) throws SQLException, IOException {
        ConcurrentHashMap<Page, Float> pageAndRelevancy = new ConcurrentHashMap<>();//страницы где встречаются леммы
        List<Page> sortedPages = new ArrayList<>();
        int[] lemmsArray = new int[lemms.size()];
        for (int i = 0; i <= lemms.size() - 1; i++) {
            lemmsArray[i] = lemms.get(i).getId();
        }
        String prepareSQl = " UNION ALL (SELECT SUM(search_index.rank) as lemmSum from search_index where page_id= %d and lemma_id = %d)";
        lemms.forEach(lemma -> {
            lemma.getUrls().forEach(page -> {
                page.getLemms().add(lemma.getName());
                StringBuilder sql = new StringBuilder("SELECT SUM(lemmSum) FROM((SELECT sum(search_index.rank) as lemmSum FROM search_index WHERE lemma_id = "
                        + lemmsArray[0] + " AND page_id = " + page.getId() + ")");
                for (int i = 1; i < lemmsArray.length; i++) {
                    sql.append(String.format(prepareSQl, page.getId(), lemmsArray[i]));
                }
                try {
                    ResultSet resultSet = statement.executeQuery(sql + ")search_index");
                    while (resultSet.next()) {
                        pageAndRelevancy.put(page, resultSet.getFloat("SUM(lemmSum)"));
                        page.setAbsRelevancy(resultSet.getFloat("SUM(lemmSum)"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            });
        });
        //=============================================================================================================================================================

        /**
         * Calculate relevancy
         */
        pageAndRelevancy.keySet().forEach(page -> {
            page.setRelevance(page.getAbsRelevancy() / Collections.max(pageAndRelevancy.values()));
            sortedPages.add(page);
        });

        return StreamEx.of(sortedPages).distinct(Page::getPath).sorted().toList();
    }


    //////////////////////////////////RANKS COLLECTED///////////////////////////////////////////////////////////////
    @Transactional
    public List<Page> getResults(SearchRequest request) {

        long start = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        List<Page> results = new ArrayList<>();
        try {
            calculateRelevancy(getListOfUrls(request)).forEach(page -> {
                Optional<Site> site = siteRepository.findById(page.getSiteid());
                page.setSiteName(site.get().getName());
                page.setSite(site.get().getUrl());
                String content = page.getContent();
                Pattern patternTitle = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
                Matcher m = patternTitle.matcher(content);
                while (m.find()) {
                    page.setTitle(m.group(1));
                }
                HashMap<String, String> wordAndLemma = null;
                content = HTMLDataFilter.findText(content);
                int contentLentgh = content.length();
                String finalContent = content;
                try {
                    wordAndLemma = Lem.replaceForLemms(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (String lemma : page.getLemms()) {

                    Pattern pattern = Pattern.compile(wordAndLemma.get(lemma));
                    Matcher matcher = pattern.matcher(finalContent.toLowerCase(Locale.ROOT));
                    int lemmIndex = 0;
                    int lastLemmIndex = 0;
                    int textBorder = 0;
                    while (matcher.find()) {
                        lemmIndex = matcher.start();
                        lastLemmIndex = matcher.end();
                        textBorder = contentLentgh - lastLemmIndex;
                    }

                    int numberOfChars = 100;
                    if (textBorder > numberOfChars) {
                        String word = finalContent.substring(lemmIndex,lastLemmIndex)+"</b>";
                        builder.append("...<b>").append(word)
                                .append(finalContent, lastLemmIndex, lastLemmIndex + numberOfChars).append("...\n");
                    } else { String word = finalContent.substring(lemmIndex,lastLemmIndex)+"</b>";
                        builder.append("...<b>").append(word)
                                .append(finalContent, lastLemmIndex, contentLentgh).append("...\n");
                    }

                }
                String snippet = builder.toString();
                page.setSnippet(snippet);
                builder.setLength(0);
                results.add(page);
                maxResults = results.size();

            });
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
        if (request.getLimit() > 0) {

            return results.stream().skip(request.getOffset()).limit(request.getLimit()).collect(Collectors.toList());
        } else {
            System.out.println(System.currentTimeMillis() - start);
            return results.stream().skip(request.getOffset()).collect(Collectors.toList()); //Page results containing calculated relevancy
        }
    }

//==============================================================================================================================================================


}
