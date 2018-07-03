package com.graphhopper.jsprit.core.DAL;

import com.graphhopper.jsprit.core.Bean.CentreConfigBean;
import com.graphhopper.jsprit.core.Bean.UserBean;
import com.graphhopper.jsprit.core.CurefitUtil.CentreConfig;
import com.graphhopper.jsprit.core.CurefitUtil.Constants;
import com.graphhopper.jsprit.core.CurefitUtil.SlotUtil;
import com.graphhopper.jsprit.core.CurefitUtil.StaticUtil;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.mongodb.*;
import javafx.util.Pair;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import static com.graphhopper.jsprit.core.CurefitUtil.StaticUtil.getDate;
import static com.graphhopper.jsprit.core.CurefitUtil.StaticUtil.getDateInEpoch;

public class MongoDao {




    private DB db;
    private String centreId;
    private String slot;
    private String deliveryChannel;
    private String deliveryDate;
    MongoClient mongo;


    public MongoDao(String centreId, String slot, String deliveryChannel, String deliveryDate) throws UnknownHostException {
        this.centreId = centreId;
        this.slot = slot;
        this.deliveryChannel = deliveryChannel;
        this.deliveryDate = deliveryDate;

        mongo = new MongoClient(Constants.MONGO_HOST , Constants.MONGO_PORT );
        this.db = mongo.getDB(Constants.MONGO_DB);



    }
    public ArrayList<UserBean> getUserData()
    {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("deliveryDate", deliveryDate);
        searchQuery.put("deliveryChannel",deliveryChannel);
        searchQuery.put("deliverySlot", new BasicDBObject("$in",SlotUtil.getSlots(slot)));
        searchQuery.put("centerId",centreId);
        searchQuery.put("osrmJobCompleted",new BasicDBObject("$exists",false));
        searchQuery.put("externalOrderInfo", new BasicDBObject("$exists",false));

        DBCollection table = db.getCollection(Constants.MONGO_FOODSHIPMENT_COLLECTION);
        DBCursor cursor = table.find(searchQuery);

        ArrayList<UserBean> userBeans = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
            Long deliveryStartTime = getDateInEpoch(((BasicDBObject)basicDBObject.get("deliveryWindow")).get("start").toString());
            String deliveryStartTimeString = ((BasicDBObject)basicDBObject.get("deliveryWindow")).get("start").toString();
            String deliveryendTimeString = ((BasicDBObject)basicDBObject.get("deliveryWindow")).get("end").toString();
            Long deliveryEndTime = getDateInEpoch(((BasicDBObject)basicDBObject.get("deliveryWindow")).get("end").toString());
            String userId = ((BasicDBObject)basicDBObject.get("userAddress")).get("userId").toString();
            Object googlePlacesId = ((BasicDBObject)basicDBObject.get("userAddress")).get("googlePlacesId");

            String deliverySlot = basicDBObject.get("deliverySlot").toString();;
            String orderId = basicDBObject.get("orderId").toString();
            String shipmentId = basicDBObject.get("shipmentId").toString();
            String cartShipmentId = basicDBObject.get("cartShipmentId").toString();
            String deliveryType = basicDBObject.get("deliveryType").toString();
            Long createdTime  = getDateInEpoch(basicDBObject.get("createdDate").toString());

            ArrayList<Double> latLong = new ArrayList<>();
            BasicDBList list = (BasicDBList)((BasicDBObject)basicDBObject.get("userAddress")).get("latLong");
            for(Object ul: list) {
                latLong.add(Double.parseDouble(ul.toString()));
            }
            if(googlePlacesId!=null)
            {
                StaticUtil.googlePlacesId.put(new Coordinate(latLong.get(0),latLong.get(1)),googlePlacesId.toString());
            }
            userBeans.add(new UserBean(userId, orderId, shipmentId, cartShipmentId, deliveryStartTime, deliveryEndTime, latLong.get(0), latLong.get(1), createdTime,deliveryType,deliverySlot,deliveryStartTimeString,deliveryendTimeString));
        }
        cursor.close();
        return userBeans;
    }


    public void setVehicleData(String centreName) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.OUTPUT_DATE_TIME_FORMAT);
//        for(String slot: new String[]{"LUNCH","BREAKFAST","SNACKS","DINNER"})
        db.getCollection(Constants.MONGO_VEHICLE).drop();

        for(String slot: new String[]{"LUNCH"})

        {
            for(int i=1;i<=(Integer) (Constants.centreConfigMap.get(centreName).get(slot+"_FLEET"));i++)
            {
                BasicDBObject vehicleObject = new BasicDBObject();
                vehicleObject.put("vehicleId",i);
                vehicleObject.put("startTime",formatter.parse("Mon May 28 11:35:00 IST 2017"));
                vehicleObject.put("endTime",formatter.parse("Mon May 28 11:35:00 IST 2017"));
                vehicleObject.put("centreName",centreName);
                vehicleObject.put("slot",slot);
                vehicleObject.put("useCount",0);
                db.getCollection(Constants.MONGO_VEHICLE).insert(vehicleObject);
            }
        }
    }

    public void setDoneJob(String cartShipmentId)
    {
        DBCollection collection = db.getCollection(Constants.MONGO_FOODSHIPMENT_COLLECTION);

        BasicDBObject searchQuery = new BasicDBObject();
        BasicDBObject updateQuery = new BasicDBObject();

        searchQuery.put("cartShipmentId", cartShipmentId);
//        searchQuery.put("deliveryDate", deliveryDate);
//        searchQuery.put("deliveryChannel",deliveryChannel);
//        searchQuery.put("deliverySlot", new BasicDBObject("$in",SlotUtil.getSlots(slot)));
//        searchQuery.put("centerId",centreId);
//        searchQuery.put("externalOrderInfo", new BasicDBObject("$exists",false));



        updateQuery.append("$set",
            new BasicDBObject().append("osrmJobCompleted", true));

        collection.updateMulti(searchQuery, updateQuery);


    }
    public void unSetDoneJob(String cartShipmentId)
    {
        DBCollection collection = db.getCollection(Constants.MONGO_FOODSHIPMENT_COLLECTION);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("cartShipmentId", cartShipmentId);
        DBCursor cursor = collection.find(searchQuery);


        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$unset",
            new BasicDBObject().append("osrmJobCompleted", false));

        collection.updateMulti(searchQuery, updateQuery);


    }
    public void resetFoodShipmentCollection()
    {
        DBCollection collection = db.getCollection(Constants.MONGO_FOODSHIPMENT_COLLECTION);
        BasicDBObject searchQuery = new BasicDBObject();
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$unset",
            new BasicDBObject().append("osrmJobCompleted", ""));

        collection.updateMulti(searchQuery, updateQuery);


    }

    public ArrayList<Integer> getAvailableVehicles(String centreName, String slot) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.OUTPUT_DATE_TIME_FORMAT);
        DBCollection collection = db.getCollection(Constants.MONGO_VEHICLE);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("centreName", centreName);
        searchQuery.put("slot", slot);
        searchQuery.put("endTime",new BasicDBObject("$lt",formatter.parse(Constants.DISPATCH_TIME_STRING)));
        DBCursor cursor = collection.find(searchQuery);
        ArrayList<Integer> vehicleList = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
            vehicleList.add( Integer.parseInt(basicDBObject.get("vehicleId").toString()));

        }
        vehicleList.sort(Comparator.naturalOrder());
        cursor.close();
        return vehicleList;

    }
    public void setVehicleAvailibility(String centreName, String slot, Integer vehicleId, Date startTime, Date endTime, Integer numberOfLotsServed)
    {

        DBCollection collection = db.getCollection(Constants.MONGO_VEHICLE);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("vehicleId", vehicleId);
        searchQuery.put("centreName", centreName);
        searchQuery.put("slot", slot);


        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set",
            new BasicDBObject().append("startTime",startTime).append("endTime", endTime)).append("$inc",new BasicDBObject("useCount",numberOfLotsServed));
        collection.updateMulti(searchQuery, updateQuery);

    }


    public void closeMongo() {
        mongo.close();

    }

    public DB getDb() {
        return db;
    }

    public static void main(String[] args) throws UnknownHostException, ParseException {
        CentreConfigBean centreConfigBean = CentreConfig.setCentreConfig("HSR","LUNCH");

        MongoDao mongoDao = new MongoDao(centreConfigBean.getCentreId(), "LUNCH", "ONLINE", Constants.DELIVERY_DATE);
        mongoDao.db.getCollection(Constants.MONGO_VEHICLE).drop();
        mongoDao.setVehicleData("HSR");
        mongoDao.resetFoodShipmentCollection();
//        mongoDao.setVehicleAvailibility(centreConfigBean.getCentreName(),"LUNCH", 1,getDate(getDateInEpoch("Mon May 28 11:35:00 IST 2018")),getDate(getDateInEpoch("Mon May 28 11:20:00 IST 2017")),5);

//        mongoDao.setDoneJob("8016590168");


    }
}
