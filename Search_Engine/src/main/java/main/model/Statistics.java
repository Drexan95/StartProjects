package main.model;

import Database.DBConnection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Statistics {

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
    @Setter
    private boolean isIndexing;
    private List<Site> detailed = new ArrayList<>();
    public Statistics(){

    }

    public void getDetailedData(Site site) throws SQLException {
      Statement statement =  DBConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT (SELECT COUNT(id) FROM lemma WHERE site_id = "+site.getId()+") AS lemmscount," +
                "(SELECT COUNT(id) FROM page WHERE site_id = "+site.getId()+") as pagecount");
        Optional<Site> siteOptional = siteRepository.findById(site.getId());
        while (resultSet.next()){
            long pageCount = resultSet.getInt("pagecount");
            long lemmsCount = resultSet.getInt("lemmscount");
            if(siteOptional.isPresent()){
                siteOptional.get().setLemmas(lemmsCount);
                siteOptional.get().setPages(pageCount);
                detailed.add(siteOptional.get());
            }

        }


    }

    public boolean isResult() {
        return result;
    }

}
