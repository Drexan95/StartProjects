package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Type;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="page")
@JsonIgnoreProperties(value = { "content","lemms","lemmRank" })
public class Page implements  Comparable<Page>{


    @javax.persistence.Id
    @Column(name ="Id")
    @Setter
    @Getter
    private int Id;
    @Column(name = "path")
    @Setter
    @Getter
    private String path;
    @Column(name = "code")
    private int code;

    @Column(name = "content")
    @JsonIgnore
    @Type(type = "text")
    private String content;

    @Column(name = "site_id")
    @Getter
    @Setter
    private Integer siteId;
    @Transient
    @Getter
    @Setter
    private String title;
    @Transient
    @Getter
    @Setter
    private String snippet;
    @Transient
    @Setter
    @Getter
    private float absRelevancy;
    @Transient
    private float comparativeRelevancy;
    @Transient
    @Getter
    @Setter
    private List<String> lemms = new ArrayList<>();;
    @Transient
    private float lemmRank;

    public Page(int Id, String path,int code,String content) {
        this.Id = Id;
        this.code = code;
        this.content = content;
        this.path = path;

    }
    public Page(String path){
        this.path = path;
    }
    public Page(){

    }


    //    @JsonIgnore
    @JsonIgnore
    public String getContent() {
        return content;
    }

    @JsonIgnore
    public void setContent(String content) {
        this.content = content;
    }


    public float getComparativeRelevancy() {
        return comparativeRelevancy;
    }

    public void setComparativeRelevancy(float comparativeRelevancy) {
        this.comparativeRelevancy = comparativeRelevancy;
    }

    @Override
    public String toString(){

        return path +"\n "+getTitle()+"\n Релевантность : "+getComparativeRelevancy()+"\n"+"Леммы :"+"\n"+lemms+"\n"+"Отрывок текста:"+"\n"+getSnippet();
    }
    @Override
    public int hashCode(){
        final int primal = 11;

        return primal+ path.hashCode();
    }
    @Override
    public  boolean equals(final  Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return  false;
        }
        if(getClass() !=obj.getClass()){
            return false;
        }
        final Page other = (Page) obj;
        if(path == null){
            if(other.path != null){
                return false;
            }
        }else if(!path.equals(other.path)){
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Page o) {
    if(this.comparativeRelevancy > o.comparativeRelevancy){
        return -1;
    }else if(this.comparativeRelevancy < o.comparativeRelevancy){
        return 1;
    }
    else return 0;

    }
}
