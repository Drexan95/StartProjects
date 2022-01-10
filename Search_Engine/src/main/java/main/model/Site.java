package main.model;

import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "site")
public class Site {


    public Site(String url){
        this.url = url;
    }
    public Site(){}

    @JsonIgnore
    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Getter
    @Setter
    private StatusType status;
    @Column(name = "status_time")
    @Getter
    @Setter
    private Date statusTime;
    @Column(name = "last_error",nullable = true)
    @Getter
    @Setter
    private String lastError;
    @Column(name = "url")
    @Getter
    @Setter
    private String url;
    @Column(name = "name")
    @Getter
    @Setter
    private String name;
    @Transient
    @Setter
    @Getter
    private long pages;
    @Transient
    @Setter
    @Getter
    private long lemmas;



}
