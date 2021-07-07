

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {


    private static String dataFile = "MoscowMap.json";

    public static void main(String[] args) throws Throwable {
        getJsonConnections(createConnections());//9.5
    }

    /**
     * ЗАДАНИЕ СО *переходы
     * @return
     * @throws IOException
     */
    public static  Map<Connection, List<Connection>> createConnections() throws IOException {
        Map<Connection,List<Connection>> connections  = new HashMap<>();
        Document doc = Jsoup.connect("https://www.moscowmap.ru/metro.html#lines").maxBodySize(0).get();
        Elements lines =doc.select("div.js-toggle-depend");
        Elements stations = doc.select("div.js-metro-stations");
        for (int i=0;i<lines.size();i++) {
            Element line = lines.get(i).selectFirst("span.js-metro-line");
            String lineNumber= line.attr("data-line");
            Elements connectedStations = stations.get(i).select("p");
            connectedStations.forEach(el -> connections.put(new Connection(
                    lineNumber,
                    el.select("span.name").text()),
                    el.select("span.t-icon-metroln")
                            .stream()
                            .map(e -> new Connection(
                                    e.attr("class")
                                            .replaceAll(".+ln-", ""),
                                    e.attr("title")
                                            .replaceAll(".+«(.+)».+", "$1")))
                            .collect(Collectors.toList())));

        }

return connections;
    }

    public static void getJsonConnections(Map<Connection, List<Connection>> map) throws IOException, JSONException {
        JSONArray jsonConnections = new JSONArray();
        JSONObject connections = new JSONObject();
        for (Map.Entry<Connection, List<Connection>> connection : map.entrySet()) {
            JSONObject cross = new JSONObject();
            Connection source = connection.getKey();
            List<Connection> ways = connection.getValue();
            cross.put(source.getNumber(), source.getName());
            jsonConnections.put(cross);
            for (Connection way : ways) {
                JSONObject jsonWay = new JSONObject();
                jsonWay.put(way.getNumber(), way.getName());
                jsonConnections.put(jsonWay);
            }
        }
        connections.put("connections",jsonConnections);

            ObjectMapper mapper = new ObjectMapper();
       mapper.enable(SerializationFeature.INDENT_OUTPUT);
      mapper.writeValue(new FileOutputStream("mskConnections.json"), map);

    }

 //========================================================================================================================================================

    public static List<String> getPureStations(String l) {
        Pattern pattern = Pattern.compile("[^\\d\\.]\\D+\\s?\\D*\\s?");
        List<String> stations = new ArrayList<>();
        Matcher matcher = pattern.matcher(l);
        while (matcher.find()) {
            stations.add(l.substring(matcher.start(), matcher.end()));
        }
        return stations;
    }

    /**
     * Создаю объекты линий метро
     * @return
     */
    public static List<Line> createLines() throws IOException {
     ArrayList<String> lines = new ArrayList<>();
        List<Line> metroMoscowLines = new ArrayList<>();
        try{
        Document doc = Jsoup.connect("https://www.moscowmap.ru/metro.html#lines").maxBodySize(0).get();
        Elements stations = doc.select("[^data-line]");//Получаю данные по линиям и станциям
        stations.forEach(element -> lines.add(element.text()));}
        catch (IOException ex){
            ex.printStackTrace();
        }

        int lineNumber = 1;
        for (int i = 0; i < lines.size(); i = i + 2) {//Первая строка название линии,следущая список станций.
            Line line = new Line(lines.get(i),
                    String.valueOf(lineNumber),
                    getPureStations(lines.get(i + 1))
            );
            metroMoscowLines.add(line);
            lineNumber += 1;
        }

        return metroMoscowLines;
    }

    /**
     * Список станций на каждой линии
     * @param l
     * @return
     */
    public static List<String> getStations(String l) {
        Pattern pattern = Pattern.compile("\\d+.\\s\\D+\\s?\\D*\\s?");
        List<String> stations = new ArrayList<>();
        Matcher matcher = pattern.matcher(l);
        while (matcher.find()) {
            stations.add(l.substring(matcher.start(), matcher.end()));
        }
        return stations;
    }
//================================================================JSON==============================================================
    /**
     * JSON массив линий
     * @param lines
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static JSONArray createJsonLine(List<Line> lines) throws JSONException, IOException {
        JSONArray jsonLines = new JSONArray();
        for (Line line : lines) {
            JSONObject metroLine = new JSONObject();
            metroLine.put("Number", line.getNumber());
            metroLine.put("name", line.getName());
            jsonLines.put(metroLine);
        }
        return jsonLines;
    }

    /**
     * JSON объект станций
     * @param lines
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject createJsonStations(List<Line> lines) throws JSONException, IOException {
        JSONObject stations = new JSONObject();
        for (Line line : lines) {
            stations.put(line.getNumber(), line.getStations());
        }

        return stations;
    }

    /**
     * Записываю JSON файл карты метро
     * @param lines
     * @throws IOException
     * @throws JSONException
     */
    public static void createMap(List<Line> lines) throws Throwable{
        JSONObject map = new JSONObject();
        map.put("lines", createJsonLine(lines));
        map.put("stations", createJsonStations(lines));
        try (FileWriter writer = new FileWriter("MoscowMap.json")) {
            ObjectMapper mapper = new ObjectMapper();
                 writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
        }
        catch (Throwable exception){
            exception.toString();
        }


    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * Получаю информацию о станциях из JSON файла в консоль
     */
    public static void getStationsFromJSON() {
        try {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonData = (org.json.simple.JSONObject) parser.parse(getJsonFile());
            org.json.simple.JSONObject stationsObject = (org.json.simple.JSONObject) jsonData.get("stations");
            parseStations(stationsObject).forEach((key, value) -> System.out.println("Номер линии: " + key + "\n" + "Станции:  " + value));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
/**
 * Получаю Map из линий и станций в качестве value
 */
    public static Map<String, List<String>> parseStations(org.json.simple.JSONObject stationsObject) {
        Map<String, List<String>> stations = new HashMap();
        stationsObject.keySet().forEach(lineNumberObject ->
        {
            String lineNumber = (String) lineNumberObject;
            org.json.simple.JSONArray stationsArray = (org.json.simple.JSONArray) stationsObject.get(lineNumberObject);
            List<String> lineStations = new ArrayList<>();
            stationsArray.forEach(stationObject ->
            {
                lineStations.add((String) stationObject);

            });
            stations.put(lineNumber, lineStations);
        });
        return stations;
    }
}

