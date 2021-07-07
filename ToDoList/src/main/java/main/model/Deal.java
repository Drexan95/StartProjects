package main.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name = "deals")
public class Deal
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer  id;
    private String name;
    private String date;
    public Deal(){
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        this.setDate(fmt.format(calendar.getTime()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
