package com.graphhopper.jsprit.core.CurefitUtil;

import com.graphhopper.jsprit.core.Bean.CentreConfigBean;

import java.util.HashMap;

public class CentreConfig {

    public static CentreConfigBean centreConfig;

    public static CentreConfigBean getCentreConfig() {
        return centreConfig;
    }

    public  static CentreConfigBean setCentreConfig(String centreName, String timeSlot)
    {
        centreConfig = new CentreConfigBean((String)Constants.centreConfigMap.get(centreName).get("CENTREID"),centreName,(Double)Constants.centreConfigMap.get(centreName).get("LONG"), (Double)Constants.centreConfigMap.get(centreName).get("LAT"),(Integer) Constants.centreConfigMap.get(centreName).get(timeSlot+"_FLEET"));

        return centreConfig;
    }

}
