package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.DAL.MongoDao;

import java.util.HashMap;

public class Constants {
    //JSPRIT CONSTANTS
    //Costs
    final public static double COST_PER_WAITING_TIME =20;
    final public static double FIXED_COST =2000;
    final public static double COST_PER_DISTANCE =20;
    final public static Integer RENDER_DELAY = 5;
    final public static Integer CAPACITY_WEIGHT = 12;
    final public static int WEIGHT_INDEX = 0;

    //threads and iterations
    public static final String NUM_OF_THREADS = "16";
    final public static Integer MAX_ITERATIONS = 16;



    //Time Constants
    public static final double MAX_TIME_IN_VEHICLE = 3300;
    public static final double PER_ORDER_DELIVERY_SERVICE_TIME = 90;
    public static final double DELIVERY_SERVICE_STATIC_TIME = 90;

    public static final double PICKUP_SERVICE_TIME = 60;
    public static final double WAITING_TIME_THRESHOLD_PER_TRIP = 600;
    public static final double INDIVIDUAL_WAITING_TIME_THRESHOLD = 240;



    //delivery slot constants
    final public static String BREAKFAST_SLOT = "BREAKFAST";
    final public static String LUNCH_SLOT = "LUNCH";
    final public static String DINNER_SLOT = "DINNER";
    final public static String SNACKS_SLOT = "SNACKS";
    final public static String DELIVERY_CHANNEL = "ONLINE";



    //date constants
    final public static String DELIVERY_DATE = "2018-07-03";
    final public static String CUT_OFF_TIME_FORMATTED_STRING = "Mon Jul 03 %s:00 IST 2018";
    public static String DISPATCH_TIME_STRING;
    public static long DISPATCH_TIME;
    static public final String INPUT_DATE_TIME_FORMAT = "MMM dd HH:mm:ss z yyyy";
    static public final String OUTPUT_DATE_TIME_FORMAT= "E MMM dd HH:mm:ss z yyyy";




    //Mongo constants
    static public final int MONGO_PORT = 27017;
    static public final String MONGO_HOST = "localhost";
    static public final String MONGO_FOODSHIPMENT_COLLECTION = "foodshipments_copy_shivek";
    static public final String MONGO_VEHICLE = "vehicle_shivek";
    static public final String MONGO_DB= "test";





    static public enum KITCHEN{
        IBLUR,
        HSR,
        IND,
        WHITEFIELD,
        BTM
    }

    public static HashMap<String, HashMap<String, Object>> centreConfigMap = new HashMap<String, HashMap<String, Object>>(){{
        this.put("HSR", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(31));
            this.put("DINNER_FLEET", new Integer(30));
            this.put("SNACKS_FLEET", new Integer(20));
            this.put("CENTREID", "S1hkbFosl");
            this.put("LONG", new Double(77.627168));
            this.put("LAT", new Double(12.912351));

        }});
        this.put("WHITEFIELD", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(40));
            this.put("DINNER_FLEET", new Integer(30));
            this.put("SNACKS_FLEET", new Integer(20));
            this.put("CENTREID", "WHITEFIELD");
            this.put("LONG", new Double("77.7097"));
            this.put("LAT", new Double("12.98287"));

        }});
        this.put("IBLUR", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(40));
            this.put("DINNER_FLEET", new Integer(30));
            this.put("SNACKS_FLEET", new Integer(20));
            this.put("CENTREID", "IBLUR");
            this.put("LONG", new Double("77.663637"));
            this.put("LAT", new Double("12.921571"));

        }});
        this.put("BTM", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(40));
            this.put("DINNER_FLEET", new Integer(30));
            this.put("SNACKS_FLEET", new Integer(20));
            this.put("CENTREID", "BTM");
            this.put("LONG", new Double("77.611853"));
            this.put("LAT", new Double("12.905463"));

        }});
        this.put("IND", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(40));
            this.put("DINNER_FLEET", new Integer(30));
            this.put("SNACKS_FLEET", new Integer(20));
            this.put("CENTREID", "IND");
            this.put("LONG", new Double("77.641533"));
            this.put("LAT", new Double("12.979152"));

        }});
    }};
    static public HashMap<String, Long> cancelCutoffTimes = new HashMap<String, Long>(){{
        this.put("LUNCH2",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"11:15")));
        this.put("LUNCH4",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"12:00")));
        this.put("LUNCH5",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"12:45")));
        this.put("L4",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"13:30")));
        this.put("KIOSK_LUNCH1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"12:00")));

        this.put("SNACKS1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"15:00")));
        this.put("SNACKS2",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"15:45")));
        this.put("SNACKS3",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"16:30")));
        this.put("KIOSK_SNACKS1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"16:00")));


        this.put("DINNER1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"18:15")));
        this.put("DINNER2",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"19:00")));
        this.put("DINNER3",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"19:45")));
        this.put("DINNER4",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"20:30")));
        this.put("KIOSK_DINNER1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"19:00")));

        this.put("B1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"06:30")));
        this.put("BREAKFAST1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"07:15")));
        this.put("BREAKFAST2",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"08:00")));
        this.put("BREAKFAST3",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"08:45")));
        this.put("KIOSK_BREAKFAST1",StaticUtil.getDateInEpoch(String.format(CUT_OFF_TIME_FORMATTED_STRING,"08:45")));


    }};


}
