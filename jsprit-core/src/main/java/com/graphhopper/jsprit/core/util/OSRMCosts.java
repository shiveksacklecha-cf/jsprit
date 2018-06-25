package com.graphhopper.jsprit.core.util;

import com.graphhopper.jsprit.core.CurefitUtil.CentreConfig;
import com.graphhopper.jsprit.core.CurefitUtil.Constants;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

public class OSRMCosts extends AbstractForwardVehicleRoutingTransportCosts {

    private double detour = 1.;
    private double speed = 4;


    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the detour factor.
     * <p>
     * The distance is calculated by the great circle distance * detour factor.
     * </p>
     *
     * @param detour
     */
    public void setDetour(double detour) {
        this.detour = detour;
    }

    private DistanceUnit distanceUnit = DistanceUnit.Meter;

    public OSRMCosts() {
        super();
    }

    public OSRMCosts(DistanceUnit distanceUnit) {
        super();
        this.distanceUnit = distanceUnit;
    }


    @Override
    public double getTransportCost(Location from, Location to, double time, Driver driver, Vehicle vehicle) {
        double distance;
        try {
            distance = calculateDistance(from, to);
        } catch (NullPointerException e) {
            throw new NullPointerException("cannot calculate euclidean distance. coordinates are missing. either add coordinates or use another transport-cost-calculator.");
        }
        double costs = distance;
        if (vehicle != null) {
            if (vehicle.getType() != null) {
                costs = distance * vehicle.getType().getVehicleCostParams().perDistanceUnit;
            }
        }
        return costs;
    }

    private double calculateDistance(Location fromLocation, Location toLocation) {
        Coordinate from = null;
        Coordinate to = null;
        if (fromLocation.getCoordinate() != null && toLocation.getCoordinate() != null) {
            from = fromLocation.getCoordinate();
            to = toLocation.getCoordinate();
        }
        if (from == null || to == null) throw new NullPointerException("either from or to location is null");
        return OSRMDistanceCalculator.calculateDistance(from, to, distanceUnit) * detour;
    }

    @Override
    public double getTransportTime(Location from, Location to, double time, Driver driver, Vehicle vehicle) {
        Double transportTime = calculateDistance(from, to) / speed;
        if(!from.equals(to) && from.equals(CentreConfig.getCentreConfig().getCentreLocation()))
            transportTime += Constants.PICKUP_SERVICE_TIME;
        return transportTime;
    }

    @Override
    public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
        return calculateDistance(from, to);
    }
}

