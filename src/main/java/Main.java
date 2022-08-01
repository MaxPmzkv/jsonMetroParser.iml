import model.Connection;
import model.Line;
import model.Metro;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;



public class Main {
    public static final String SITE_URL = "https://www.moscowmap.ru/metro.html#lines" ;
    public static final String JSON_FILE_PATH = "data/map.json";

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            Document doc= Jsoup.connect(SITE_URL).maxBodySize(0).get();
            Elements table = doc.select("div.t-text-simple");
            List<String> uniqueLineNumbers = table.select("div.js-metro-stations").eachAttr("data-line");
            LinkedHashMap<String, JSONArray> linesWithStationMap = Parser.getLinesWithStations();
            List<Line> linesList = new ArrayList<>();
            for(int i = 0; i < uniqueLineNumbers.size(); i++) {
                linesList.add(new Line(uniqueLineNumbers.get(i), Parser.parseLinesNames(table).get(i)));

            }


            Metro metro = new Metro(linesWithStationMap, linesList, getConsWithStations(doc) );
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE_PATH), metro);
            Parser.parsePrintFromJSON();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private static List<List<Connection>> getConsWithStations(Document doc)   {
        List<List<Connection>> connections = new ArrayList<>();
        Elements elements = doc.select("div.js-metro-stations");
        for (Element element : elements)    {
            String stationLineFrom = element.attr("data-line");
            Elements stationElements = element.select("a");
            for (Element stationElement : stationElements)  {
                String stationNameFrom = stationElement.select("span.name").text();
                Elements elementsConnectStations = null;
                try {
                    elementsConnectStations = stationElement.select("span.t-icon-metroln");
                } catch (Exception ex)  {}
                if (elementsConnectStations.size() != 0) {
                    Connection connectionFrom = new Connection(stationLineFrom, stationNameFrom);
                    List<Connection> connectionArray = new ArrayList<>();
                    connectionArray.add(connectionFrom);
                    for (Element elementConnect : elementsConnectStations)  {
                        String stationLineToTag = elementConnect.selectFirst("span.t-icon-metroln").toString();
                        String stationLineTo = stationLineToTag.substring(31, stationLineToTag.lastIndexOf(" title=")-1);
                        String stationNameTo = elementConnect. attr("title").replaceAll(".+«(.+)».+", "$1");
                        Connection connectionTo = new Connection(stationLineTo, stationNameTo);
                        connectionArray.add(connectionTo);
                    }
                    connections.add(connectionArray);
                }
            }
        }
        return connections;
    }


    }











