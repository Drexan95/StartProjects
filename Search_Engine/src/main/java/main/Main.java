package main;

import Database.DBConnection;
import UrlService.HTMLDataFilter;
import main.controllers.DefaultController;
import main.controllers.PageController;
import main.controllers.SiteController;
import main.model.SearchIndex;
import main.model.Statistics;
import main.repository.SearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;


@SpringBootApplication(scanBasePackages = {"resources.static", "main.model"})
@EnableJpaRepositories
@EnableConfigurationProperties()
@ComponentScan(basePackageClasses = {PageController.class,
        SiteController.class,
        DefaultController.class,
        UrlService.URLCollector.class,
        HTMLDataFilter.class,
        main.ManagementCommands.class})
public class Main {



    public static void main(String[] args) throws SQLException, IOException {
        SpringApplication.run(Main.class, args);



    }


}
