package com.graphhopper.jsprit.core.DAL;

import com.graphhopper.jsprit.core.Bean.UserBean;
import com.graphhopper.jsprit.core.CurefitUtil.Constants;
import com.graphhopper.jsprit.core.CurefitUtil.SlotUtil;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class MongoDao {




    private DB db;
    private String centreId;
    private String slot;
    private String deliveryChannel;
    private String deliveryDate;


    public MongoDao(String centreId, String slot, String deliveryChannel, String deliveryDate) throws UnknownHostException {
        this.centreId = centreId;
        this.slot = slot;
        this.deliveryChannel = deliveryChannel;
        this.deliveryDate = deliveryDate;

        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient(Constants.MONGO_HOST , Constants.MONGO_PORT );
        this.db = mongo.getDB(Constants.MONGO_DB);



    }
    public ArrayList<UserBean> getUserData()
    {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("deliveryDate", deliveryDate);
        searchQuery.put("deliveryChannel",deliveryChannel);
        searchQuery.put("deliverySlot", new BasicDBObject("$in",SlotUtil.getSlots(slot)));
        searchQuery.put("centerId",centreId);
        DBCollection table = db.getCollection(Constants.MONGO_COLLECTION);
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
            String deliveryType = basicDBObject.get("deliveryType").toString();
            Long createdTime  = getDate(basicDBObject.get("createdDate").toString());
//            if(createdTime <= Constants.DISPATCH_TIME)
//            {
//                System.out.println("created time: "+ basicDBObject.get("createdDate"));
//                System.out.println("start time: "+((BasicDBObject)basicDBObject.get("deliveryWindow")).get("start").toString());
//                System.out.println("end time: "+((BasicDBObject)basicDBObject.get("deliveryWindow")).get("end").toString());
//
//            }





            ArrayList<Double> latLong = new ArrayList<>();
            BasicDBList list = (BasicDBList)((BasicDBObject)basicDBObject.get("userAddress")).get("latLong");
            for(Object ul: list) {
                latLong.add(Double.parseDouble(ul.toString()));
            }
            userBeans.add(new UserBean(userId, orderId, shipmentId, cartShipmentId, deliveryStartTime, deliveryEndTime, latLong.get(0), latLong.get(1), createdTime,deliveryType));


        }
        cursor.close();
        return userBeans;
    }
    public static Long getDate(String date)
    {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT, Locale.ENGLISH);
        try {

            ZonedDateTime s = ZonedDateTime.parse(date.substring(4), inputFormatter);
            return s.toEpochSecond();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0L;
        }

    }


}
