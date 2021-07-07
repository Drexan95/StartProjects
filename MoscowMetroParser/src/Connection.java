import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class Connection {
    private String lineNumber;
    private String stationName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
   private List<Connection> connections;

    public Connection(String lineNumber, String stationName) {
        this.lineNumber = lineNumber;
        this.stationName = stationName;
    }
    public String getName(){
        return stationName;
    }
    public String getNumber(){
        return  lineNumber;
    }
    @Override
    public String toString(){
        return lineNumber+"."+stationName;
    }




}
