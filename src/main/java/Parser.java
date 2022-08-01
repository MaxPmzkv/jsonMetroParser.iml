import model.Connection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private static final String SITE_URL = "https://www.moscowmap.ru/metro.html#lines" ;
    private static final String JSON_FILE_PATH = "data/map.json";

    static LinkedHashMap<String, JSONArray> getLinesWithStations() throws IOException {
        Document document = Jsoup.connect(SITE_URL).maxBodySize(0).get();
        String query = "div[class$=t-metrostation-list-table]";
        LinkedHashMap<String, JSONArray> linesWithStationsMap = new LinkedHashMap<>();
        Elements lines = document.select(query);

        for(Element line : lines){
            String lineNum  = line.select("div[class^=js-metro-stations]").attr("data-line");
            JSONArray stationInLine = getStationInline(line);
            linesWithStationsMap.put(lineNum, stationInLine);

        }

        return linesWithStationsMap;

    }




    static JSONArray getStationInline(Element line){
        JSONArray stationInLine = new JSONArray();
        Elements stations = line.getElementsByTag("a");
        for(Element station : stations){
            String stationName = station.select("span[class=name]").text();
            stationInLine.add(stationName);

        }
        return stationInLine;
    }
    static List<String> parseLinesNames(Elements table){
        List<String> lineNames = new ArrayList<>();
        Elements lines = table.select("span.js-metro-line");
        for (Element line : lines){
            lineNames.add((line).text());
        }
        return lineNames.stream().distinct().collect(Collectors.toList());
    }
    static void parsePrintFromJSON() {

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());
            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            JSONArray connectionsObject = (JSONArray) jsonData.get("connections");

            Set<Object> stationNumbers = new TreeSet<>(stationsObject.keySet());


            for (Object station : stationNumbers) {
                System.out.println("Линия № " + station + " содержит станций: " + new ArrayList<>((Collection<String>) stationsObject.get(station)).size());
            }

            System.out.println("Количество пересадок: " + connectionsObject.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(JSON_FILE_PATH));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }






}
