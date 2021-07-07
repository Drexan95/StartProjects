import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
public class Loader
{
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static HashMap<Integer, WorkTime> voteStationWorkTimes = new HashMap<>();
    private static HashMap<Voter, Integer> voterCounts = new HashMap<>();

    static StringBuilder sql = new StringBuilder();
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        String fileName = "C:\\Users\\admin\\IdeaProjects\\java_basics\\16_Perfomance\\VoteAnalyzerOpt\\VoteAnalyzer\\res\\data-18M.xml";
        DBConnection.getConnection();
        saxParseFile(fileName);
        DBConnection.printVoterCounts();
        DBConnection.getConnection().close();
        System.out.println(System.currentTimeMillis() - start);

    }

    /**
     * SAX
     * @param fileName
     * @throws Exception
     */

    private static void saxParseFile(String fileName) throws Exception{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XMLHandler handler = new XMLHandler();
        parser.parse(fileName, handler);

        DBConnection.executeInsertVoters();//Добавляю оставшиеся из батча


    }
//========================================================================================//
    /**
     * DOM
     * @param fileName
     * @throws Exception
     */

    private static void parseFile(String fileName) throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        findEqualVoters(doc);
       fixWorkTimes(doc);
    }

    private static void findEqualVoters(Document doc) throws Exception
    {
        NodeList voters = doc.getElementsByTagName("voter");
        for(int i = 0; i < voters.getLength(); i++)
        {
            Node node = voters.item(i);
            NamedNodeMap attributes = node.getAttributes();
            DBConnection.countVoter(attributes.getNamedItem("name").getNodeValue(),
                    attributes.getNamedItem("birthDay").getNodeValue());

            Voter voter = new Voter(attributes.getNamedItem("name").getNodeValue()
                    , attributes.getNamedItem("birthDay").getNodeValue());
            int count = voterCounts.getOrDefault(voter,0);
            voterCounts.put(voter, count == 0 ? 1 : count + 1);
        }


    }

    private static void fixWorkTimes(Document doc) throws Exception
    {
        NodeList visits = doc.getElementsByTagName("visit");
        for(int i = 0; i < visits.getLength(); i++)
        {
            Node node = visits.item(i);
            NamedNodeMap attributes = node.getAttributes();

           int station = Integer.parseInt(attributes.getNamedItem("station").getNodeValue());
            Date time = visitDateFormat.parse(attributes.getNamedItem("time").getNodeValue());
            WorkTime workTime = voteStationWorkTimes.get(station);
            if(workTime == null)
            {
                workTime = new WorkTime();
                voteStationWorkTimes.put(station, workTime);
            }
            workTime.addVisitTime(time.toString());
        }
    }
}