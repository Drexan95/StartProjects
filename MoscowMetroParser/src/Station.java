public class Station {
    private Line line;
    private String name;

    public Station(String name,Line line){
        this.name=name;
        this.line=line;
    }
    public Line getLine(String name)
    {

        this.name = name;
        return line;
    }

    public String getName()
    {
        return name;
    }
    @Override
    public String toString()
    {
        return name;
    }
}
