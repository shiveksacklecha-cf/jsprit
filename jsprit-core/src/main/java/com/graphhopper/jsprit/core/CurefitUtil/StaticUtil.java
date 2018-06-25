package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.DAL.SerializerUtil;
import com.graphhopper.jsprit.core.util.Coordinate;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StaticUtil {
    public static Map<Pair<Coordinate,Coordinate>, Double> distanceMatrix;
    public static ArrayList<Double> waitingTimeForVehicles;

    public StaticUtil(String centreName) {
        distanceMatrix = (Map<Pair<Coordinate,Coordinate>, Double>) SerializerUtil.deseriaLizeObject("osrm_distances_" + centreName);
        if (distanceMatrix == null)
            distanceMatrix = new HashMap<>();
        waitingTimeForVehicles = new ArrayList<>();
    }

    public static void main(String[] args) {
        new StaticUtil("HSR");
    }
}
