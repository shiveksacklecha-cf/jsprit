package com.graphhopper.jsprit.core.Bean;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.util.Coordinate;

public class CentreConfigBean {
    String centreId;
    String centreName;
    Double centreLat;
    Double centreLong;
    Location centreLocation;
    Coordinate centreCoordinate;

    public Integer getFleetSize() {
        return fleetSize;
    }

    Integer fleetSize;

    public String getCentreId() {
        return centreId;
    }

    public String getCentreName() {
        return centreName;
    }

    public Double getCentreLat() {
        return centreLat;
    }

    public Double getCentreLong() {
        return centreLong;
    }

    public Coordinate getCentreCoordinate() {
        return centreCoordinate;
    }

    public CentreConfigBean(String centreId, String centreName, Double centreLong, Double centreLat , Integer fleetSize) {

        this.centreId = centreId;
        this.centreName = centreName;
        this.centreLat = centreLat;
        this.centreLong = centreLong;
        this.fleetSize = fleetSize;
        this.centreLocation = Location.newInstance(centreLong,centreLat);
        this.centreCoordinate = new Coordinate(centreLong,centreLat);

    }

    public Location getCentreLocation() {
        return centreLocation;
    }
}
