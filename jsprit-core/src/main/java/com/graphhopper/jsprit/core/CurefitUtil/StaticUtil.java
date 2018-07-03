package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.Bean.CartShipment;
import com.graphhopper.jsprit.core.Bean.CentreConfigBean;
import com.graphhopper.jsprit.core.DAL.SerializerUtil;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupShipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.Coordinate;
import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticUtil {
    public static Map<Pair<Coordinate,Coordinate>, Double> distanceMatrix;
    public static  Map<String,CartShipment> cartShipmentIdToCShipment;
    public static CentreConfigBean centreConfigBean;
    public static HashMap<Coordinate,String> googlePlacesId;

    public StaticUtil(CentreConfigBean centreConfigBean) {
        distanceMatrix = (Map<Pair<Coordinate,Coordinate>, Double>) SerializerUtil.deseriaLizeObject("osrm_distances_" + centreConfigBean.getCentreName());
        if (distanceMatrix == null)
            distanceMatrix = new HashMap<>();
        cartShipmentIdToCShipment = new HashMap<>();
        googlePlacesId = new HashMap<>();
        this.centreConfigBean = centreConfigBean;
    }

    public static Long getDateInEpoch(String date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.INPUT_DATE_TIME_FORMAT);
        try {
            Date date1 = formatter.parse(date.substring(4));
            return date1.getTime()/1000;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0L;
        }

    }

    public static Date getDate(Long epochDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.OUTPUT_DATE_TIME_FORMAT);
        return formatter.parse(formatter.format(new Date(epochDate*1000)));
    }
    public static HardActivityConstraint.ConstraintsStatus checkStack(List<TourActivity> tourActivityList)
    {
        int n = tourActivityList.size(),sum=0;
        Boolean closeFlagEnabled= false;
        for(int i =0 ;i<n;i++)
        {
            if(sum ==0)
                closeFlagEnabled=false;
            if(closeFlagEnabled)
            {
                if(tourActivityList.get(i) instanceof PickupShipment)
                    return HardActivityConstraint.ConstraintsStatus.NOT_FULFILLED;
                else
                    sum -=1;
            }
            else {
                if(tourActivityList.get(i) instanceof PickupShipment)
                    sum+=1;
                else
                {
                    sum -=1;
                    closeFlagEnabled = true;
                }
            }
            if(sum ==0)
                closeFlagEnabled=false;

        }
        if(sum<0)
            return HardActivityConstraint.ConstraintsStatus.NOT_FULFILLED;
        return HardActivityConstraint.ConstraintsStatus.FULFILLED;

    }


    public static void main(String[] args) throws ParseException {
        System.out.println(getDate(getDateInEpoch("Mon May 28 11:35:00 IST 2018")));
    }

}
