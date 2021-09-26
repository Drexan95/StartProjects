package main;

import Database.DBConnection;
import Lemmatizer.Lem;
import UrlService.URLCollector;
import com.sun.corba.se.spi.orb.DataCollector;
import main.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;


@SpringBootApplication(scanBasePackages = {"resources.static","main.model"})
@EnableJpaRepositories
@EnableConfigurationProperties()
@ComponentScan(basePackageClasses = {main.PageController.class,
        main.SiteController.class,
        main.DefaultController.class,
        UrlService.URLCollector.class,
        UrlService.Helper.class})
public class Main {
    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws SQLException, IOException {
       SpringApplication.run(Main.class, args);











    }

}
