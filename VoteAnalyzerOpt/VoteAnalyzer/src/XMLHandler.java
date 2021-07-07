import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;

public class XMLHandler  extends DefaultHandler {


    public XMLHandler()  {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {

            if (qName.equals("voter")) {
                String birthDay = attributes.getValue("birthDay");
                birthDay = birthDay.replace('.', '-');
               DBConnection.countVoter(attributes.getValue("name"),birthDay);
            }
            else if (qName.equals("visit") ) {

                short station = Short.parseShort(attributes.getValue("station"));
                String time = attributes.getValue("time");
                time = time.replace('.', '-');
                DBConnection.countWorkTime(station,time);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }



}
