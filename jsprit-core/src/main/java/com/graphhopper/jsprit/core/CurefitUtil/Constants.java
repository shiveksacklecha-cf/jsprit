package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.DAL.MongoDao;

import java.util.HashMap;

public class Constants {
    final public static Integer MAX_ITERATIONS = 2;
    final public static String BREAKFAST_SLOT = "BREAKFAST";
    final public static String LUNCH_SLOT = "LUNCH";
    final public static String DINNER_SLOT = "DINNER";
    final public static String SNACKS_SLOT = "SNACKS";
    final public static String DELIVERY_CHANNEL = "ONLINE";
    final public static String DELIVERY_DATE = "2018-05-28";
    final public static Integer RENDER_DELAY = 5;
    final public static Integer CAPACITY_WEIGHT = 12;
    final public static int WEIGHT_INDEX = 0;
    final public static long DISPATCH_TIME = MongoDao.getDate("Tue May 28 11:55:00 IST 2018");
    public static final double MAX_TIME_IN_VEHICLE = 3000;
    static public final int MONGO_PORT = 27017;
    static public final String MONGO_HOST = "localhost";
    static public final String MONGO_COLLECTION = "foodshipments";
    static public final String MONGO_DB= "test";
    static public final String DATE_TIME_FORMAT= "MMM dd HH:mm:ss zzz yyyy";
    public static final double DELIVERY_SERVICE_TIME = 360;
    public static final double PICKUP_SERVICE_TIME = 240;
    public static final String NUM_OF_THREADS = "16";

    static public enum KITCHEN{
        IBLUR,
        HSR,
        IND,
        WHITEFIELD,
        BTM
    }

    static HashMap<String, HashMap<String, Object>> centreConfigMap = new HashMap<String, HashMap<String, Object>>(){{
        this.put("HSR", new HashMap<String, Object>(){{
            this.put("BREAKFAST_FLEET", new Integer(15));
            this.put("LUNCH_FLEET", new Integer(40));
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


}
