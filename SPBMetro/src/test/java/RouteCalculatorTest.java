import junit.framework.TestCase;
import  core.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RouteCalculatorTest extends TestCase {
    List<Station> connect1;
    List<Station> connect2;
    List<Station> route;
    List<Station> shortRoute;
    StationIndex stationIndex ;
    RouteCalculator routeCalculator;

    @Override
    protected void setUp() throws Exception {
        connect1 = new ArrayList<>();
        connect2 = new ArrayList<>();
        route = new ArrayList<>();
        shortRoute = new ArrayList<>();
        stationIndex = new StationIndex();


        Line line1 = new Line(1,"Первая");
        Line line2 = new Line(2,"Вторая");
        Line line3 = new Line(3,"Третья");

        Station station1 = new Station("Александровская",line1);
        Station station2 = new Station("Николаевская",line1);
        Station station11 = new Station("Пушкинская",line1);
        Station station3 = new Station("Петровская",line2);
        Station station4 = new Station("Екатеринская",line2);
        Station station5 = new Station("Елизаветская",line3);
        Station station6 = new Station("Олеговская",line3);
        Station station7 = new Station("Борисовская",line3);

        stationIndex.addLine(line1);
        stationIndex.addLine(line2);
        stationIndex.addLine(line3);
        line1.addStation(station1);
        line1.addStation(station2);
        line1.addStation(station11);
        line2.addStation(station4);
        line2.addStation(station3);
        line3.addStation(station5);
        line3.addStation(station6);
        line3.addStation(station7);

        stationIndex.addStation(station1);
        stationIndex.addStation(station2);
        stationIndex.addStation(station11);
        stationIndex.addStation(station3);
        stationIndex.addStation(station4);
        stationIndex.addStation(station5);
        stationIndex.addStation(station6);
        stationIndex.addStation(station7);

        connect1.add(station2);
        connect1.add(station3);
        connect2.add(station4);
        connect2.add(station5);
        stationIndex.addConnection(connect1);
        stationIndex.addConnection(connect2);

        routeCalculator = new RouteCalculator(stationIndex);
        route.add(station1);
        route.add(station2);
        route.add(station4);
        route.add(station6);

    }
    protected void tearDown() throws Exception{

    }

    public void testCalculateDuration()
    {
    double actual = RouteCalculator.calculateDuration(route);
    double expected = 9.5;
    assertEquals(expected,actual);
    }

    public void testGetRoutOnTheLine()
    {

 List<Station> actual = routeCalculator.getShortestRoute(stationIndex.getStation("Александровская"),stationIndex.getStation("Пушкинская"));
 List<Station> expected = Arrays.asList(stationIndex.getStation("Александровская"),
         stationIndex.getStation("Николаевская"),stationIndex.getStation("Пушкинская"));
 assertEquals(expected,actual);
    }

    public void testGetRouteWithOneConnection()
    {
        List<Station> actual = routeCalculator.getShortestRoute(stationIndex.getStation("Александровская"),stationIndex.getStation("Екатеринская"));
        List<Station> expected = Arrays.asList(stationIndex.getStation("Александровская"),stationIndex.getStation("Николаевская"),
                stationIndex.getStation("Петровская"),stationIndex.getStation("Екатеринская"));
        assertEquals(expected,actual);
    }

    public void testGetRouteWithTwoConnections()
    {
        List<Station> actual = routeCalculator.getShortestRoute(stationIndex.getStation("Александровская"),stationIndex.getStation("Олеговская"));
        List<Station> expected = Arrays.asList(stationIndex.getStation("Александровская"),stationIndex.getStation("Николаевская"),
                stationIndex.getStation("Петровская"),stationIndex.getStation("Екатеринская"),stationIndex.getStation("Елизаветская"),
                stationIndex.getStation("Олеговская"));
        assertEquals(expected,actual);
    }
}
