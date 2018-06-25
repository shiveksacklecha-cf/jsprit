package com.graphhopper.jsprit.core.util;

import com.graphhopper.jsprit.core.CurefitUtil.CurlUtil;
import com.graphhopper.jsprit.core.CurefitUtil.StaticUtil;
import com.graphhopper.jsprit.core.DAL.RedisDao;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OSRMDistanceCalculator {

    private static final double R = 6372.8; // km

    /**
     * Harversine method.
     * <p>
     * double lon1 = coord1.getX();
     * double lon2 = coord2.getX();
     * double lat1 = coord1.getY();
     * double lat2 = coord2.getY();
     *
     * @param coord1 - from coord
     * @param coord2 - to coord
     * @return great circle distance
     */

    public static double calculateDistance(Coordinate coord1, Coordinate coord2, DistanceUnit distanceUnit) {
        double lon1 = coord1.getX();
        double lon2 = coord2.getX();
        double lat1 = coord1.getY();
        double lat2 = coord2.getY();


        Double distance = null;
        try
        {
            if(!StaticUtil.distanceMatrix.containsKey(new Pair<>(coord1,coord2)))
            {
                String distanceFromRedis = RedisDao.hget("osrm",RedisDao.getRedisLocationField(coord1,coord2));
                if(distanceFromRedis == null)
                {
                    System.out.println("coming from api " + coord1 +","+ coord2);
                    ArrayList<Double> distances = CurlUtil.getDistance(lon1,lat1,lon2,lat2);
                    StaticUtil.distanceMatrix.put(new Pair<>(coord1,coord1),distances.get(0));
                    StaticUtil.distanceMatrix.put(new Pair<>(coord1,coord2),distances.get(1));
                    StaticUtil.distanceMatrix.put(new Pair<>(coord2,coord1),distances.get(2));
                    StaticUtil.distanceMatrix.put(new Pair<>(coord2,coord2),distances.get(3));

                    RedisDao.hset("osrm",RedisDao.getRedisLocationField(coord1,coord1),distances.get(0).toString());
                    RedisDao.hset("osrm",RedisDao.getRedisLocationField(coord1,coord2),distances.get(1).toString());
                    RedisDao.hset("osrm",RedisDao.getRedisLocationField(coord2,coord1),distances.get(2).toString());
                    RedisDao.hset("osrm",RedisDao.getRedisLocationField(coord2,coord2),distances.get(3).toString());
                }
                else
                {
                    System.out.println("coming from redis " + coord1 +","+ coord2);
                    StaticUtil.distanceMatrix.put(new Pair<>(coord1,coord2),Double.parseDouble(distanceFromRedis));

                }
            }
            distance = StaticUtil.distanceMatrix.get(new Pair<>(coord1,coord2));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            double delta_Lat = Math.toRadians(lat2 - lat1);
            double delta_Lon = Math.toRadians(lon2 - lon1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            double a = Math.sin(delta_Lat / 2) * Math.sin(delta_Lat / 2) + Math.sin(delta_Lon / 2) * Math.sin(delta_Lon / 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            distance = R * c;
            if (distanceUnit.equals(DistanceUnit.Meter)) {
                distance = distance * 1000.;
            }
        }

        return distance;
    }


}
