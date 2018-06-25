package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.util.Coordinate;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CurlUtil {
    static Map<Pair<Coordinate, Coordinate>, Double> distanceMatrix = new HashMap<>();

    public CurlUtil() {

    }

    public static ArrayList<Double> getDistance(Double lon1, Double lat1, Double lon2, Double lat2) throws Exception {


        String url = "http://127.0.0.1:5000/table/v1/driving/" + lon1 + "," + lat1 + ";" + lon2 + "," + lat2 + "?annotations=distance";

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status != 200)
            throw new HTTPException(status);

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content.toString());
        JSONArray jsonArray = (JSONArray) json.get("distances");
        Double distanceaa = Double.parseDouble(((JSONArray) jsonArray.get(0)).get(0).toString());
        Double distanceab = Double.parseDouble(((JSONArray) jsonArray.get(0)).get(1).toString());
        Double distanceba = Double.parseDouble(((JSONArray) jsonArray.get(1)).get(0).toString());
        Double distancebb = Double.parseDouble(((JSONArray) jsonArray.get(1)).get(1).toString());

        con.disconnect();
        ArrayList<Double> distances = new ArrayList<>();
        distances.add(distanceaa);
        distances.add(distanceab);
        distances.add(distanceba);
        distances.add(distancebb);


        return distances;

    }


}
