package main.model;

import lombok.Getter;
import lombok.Setter;
import main.repository.SearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Statistics {
    public Statistics(){

    }

//    @Getter
//    @Setter
//    private boolean result = true;
    @Autowired
    @Getter
    @Setter
 private    Total total;
    @Getter
    @Setter
    private final List<Site> detailed = new ArrayList<>();

//    public Statistics getStatistics(){
//
//        return this;
//    }

}
