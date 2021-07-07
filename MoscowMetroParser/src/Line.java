import java.util.ArrayList;
import java.util.List;

public class Line {
    protected String number;
    protected String name;
    protected List<String> stations;
    public Line(String name,String number,List<String> stations){
        this.name = name;
        this.number = number;
        this.stations = stations;
    }
    public String getName(){
        return name;
    }
    public String getNumber(){
        return  number;
    }
    public List<String> getStations(){
        return stations;
    }
    @Override
    public String toString(){
        return "Название "+ getName() + " \n" +"Номер линии " + number + " Станции:  "+ stations ;
    }
}
