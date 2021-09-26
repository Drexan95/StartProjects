package main.model;

import javax.persistence.*;

@Entity
@Table(name = "search_index")
public class SearchIndex {
public SearchIndex(){

}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer Id;
    @Column(name = "page_id")
    private int pageId;
    @Column(name = "lemma_id")
    private int lemmaId;
    @Column(name = "lemma_rank")
    private float lemmaRank;

    public SearchIndex(int pageId,int lemmaId,float lemmaRank){
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.lemmaRank = lemmaRank;
    }


}
