package com.graphhopper.jsprit.core.DAL;

import com.graphhopper.jsprit.core.Bean.UserBean;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class MongoDao {

    private DB db;

    public MongoDao() throws UnknownHostException {

        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        this.db = mongo.getDB("test");



    }
    public ArrayList<UserBean> getUserData()
    {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("deliveryDate", "2018-05-28");
        searchQuery.put("deliveryChannel","ONLINE");
        ArrayList<String> slotList = new ArrayList<>();
        slotList.add("BREAKFAST1");
        slotList.add("BREAKFAST2");
        slotList.add("BREAKFAST3");
        slotList.add("B1");
        searchQuery.put("deliverySlot", new BasicDBObject("$in",slotList ));
        searchQuery.put("centerId","S1hkbFosl");
        DBCollection table = db.getCollection("foodshipments");
        DBCursor cursor = table.find(searchQuery);

        ArrayList<UserBean> userBeans = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
            Long deliveryStartTime = getDate(((BasicDBObject)basicDBObject.get("deliveryWindow")).get("start").toString());
            Long deliveryEndTime = getDate(((BasicDBObject)basicDBObject.get("deliveryWindow")).get("end").toString());
            String userId = ((BasicDBObject)basicDBObject.get("userAddress")).get("userId").toString();
            String orderId = basicDBObject.get("orderId").toString();
            String shipmentId = basicDBObject.get("shipmentId").toString();
            String cartShipmentId = basicDBObject.get("cartShipmentId").toString();


            ArrayList<Double> latLong = new ArrayList<>();
            BasicDBList list = (BasicDBList)((BasicDBObject)basicDBObject.get("userAddress")).get("latLong");
            for(Object ul: list) {
                latLong.add(Double.parseDouble(ul.toString()));
            }
            userBeans.add(new UserBean(userId, orderId, shipmentId, cartShipmentId, deliveryStartTime, deliveryEndTime, latLong.get(0), latLong.get(1)));


        }
        cursor.close();
        return userBeans;
    }
    public static Long getDate(String date)
    {
        System.out.println(date);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        try {

            ZonedDateTime s = ZonedDateTime.parse(date, inputFormatter);
            System.out.println(s.toEpochSecond());
            return s.toEpochSecond();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0L;
        }

    }

    public static void main(String[] args) throws UnknownHostException {
        new MongoDao();
    }
}
