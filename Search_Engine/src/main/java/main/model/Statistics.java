package main.model;

import Database.DBConnection;
import lombok.Getter;
import lombok.Setter;
import main.repository.FieldRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class Statistics {

    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private SiteRepository siteRepository;
    private boolean result;
    @Getter
    @Setter
    private  long sites;
    @Getter
    @Setter
    private  long pages;
    @Setter
    @Getter
    private long lemmas;
    @Getter
    private final List<Site> detailed = new ArrayList<>();

    public void getDetailedData(Site site) throws SQLException {
      Statement statement =  DBConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT (SELECT COUNT(id) FROM lemma WHERE site_id = "+site.getId()+") AS lemmscount," +
                "(SELECT COUNT(id) FROM page WHERE site_id = "+site.getId()+") as pagecount");
//        Optional<Site> siteOptional = siteRepository.findById(site.getId());
        while (resultSet.next()){
            long pageCount = resultSet.getInt("pagecount");
            long lemmsCount = resultSet.getInt("lemmscount");

                site.setLemmas(lemmsCount);
                site.setPages(pageCount);
                detailed.add(site);


        }


    }

    public boolean isResult() {
        return result;
    }

}
