package com.graphhopper.jsprit.core.DAL;

import com.graphhopper.jsprit.core.util.Coordinate;
import redis.clients.jedis.Jedis;

import java.awt.*;

public class RedisDao {
    public static Jedis jedis;
    final static String STRING_SEPAREATOR = ",";


    public RedisDao() {

        jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");

    }

    public static void hset(String key, String field, String value) {
        jedis.hset(key,field,value);
    }
    public static String hget(String key, String field) {
        return jedis.hget(key,field);
    }


    public void closeJedis() {
        jedis.close();
    }

    public static String getRedisLocationField(Coordinate c1,Coordinate c2)
    {
        return c1.getX() + STRING_SEPAREATOR + c1.getY() + STRING_SEPAREATOR +c2.getX() +STRING_SEPAREATOR + c2.getY();
    }
}

