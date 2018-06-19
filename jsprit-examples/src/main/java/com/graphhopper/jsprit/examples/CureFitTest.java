package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.core.Bean.CartShipment;
import com.graphhopper.jsprit.core.Bean.UserBean;
import com.graphhopper.jsprit.core.DAL.MongoDao;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.GreatCircleCosts;
import com.graphhopper.jsprit.core.util.Solutions;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CureFitTest {
    public static void main(String[] args) throws UnknownHostException {

        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
         */
        final int WEIGHT_INDEX = 0;
        final int NUM_LOCATIONS_INDEX = 0;

        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
            .addCapacityDimension(WEIGHT_INDEX, 12)
            .addCapacityDimension(NUM_LOCATIONS_INDEX,5);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for( int i=0;i<15; i++)
        {
            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("v: " + i);
            vehicleBuilder.setStartLocation(Location.newInstance(77.627168, 12.912351));
            vehicleBuilder.setType(vehicleType);
//            vehicleBuilder.setReturnToDepot(false);
            vehicleBuilder.setEarliestStart(MongoDao.getDate("Tue May 28 06:30:00 IST 2018"));
            VehicleImpl vehicle = vehicleBuilder.build();
            vrpBuilder.addVehicle(vehicle);

        }




        /*
         * build services at the required locations, each with a capacity-demand of 1.
         */
        ArrayList<Delivery> deliveries
            = new ArrayList<>();

        MongoDao mongoDao = new MongoDao();
        ArrayList<UserBean> userBeans = mongoDao.getUserData();

        Map<String,CartShipment> cartShipmentIdToCShipment = new HashMap<>();
        for(UserBean userBean:userBeans)
        {
            if(cartShipmentIdToCShipment.containsKey(userBean.getCartShipmentId()))
            {
                cartShipmentIdToCShipment.get(userBean.getCartShipmentId()).addShipment(userBean.getShipmentId());
            }
            else
            {
                CartShipment cartShipment = new CartShipment(userBean.getUserId(), userBean.getOrderId(), userBean.getCartShipmentId(), userBean.getDeliveryStartTime(), userBean.getDeliveryEndTime(), userBean.getLatitude(), userBean.getLongitude());
                cartShipment.addShipment(userBean.getShipmentId());
                cartShipmentIdToCShipment.put(userBean.getCartShipmentId(), cartShipment);
            }
        }
        for (CartShipment cartShipment :cartShipmentIdToCShipment.values())
        {
            Delivery delivery = Delivery.Builder.newInstance(cartShipment.getCartShipmentId())
                .setServiceTime(360)
                .setMaxTimeInVehicle(3000)
                .addTimeWindow(cartShipment.getDeliveryStartTime(),cartShipment.getDeliveryEndTime())
                .addSizeDimension(WEIGHT_INDEX, cartShipment.getNumberOfShipments())
                .addSizeDimension(NUM_LOCATIONS_INDEX, 1)
                .setLocation(Location.newInstance(cartShipment.getLatitude(), cartShipment.getLongitude())).build();
            deliveries.add(delivery);
        }





        for(Delivery delivery : deliveries)
            vrpBuilder.addJob(delivery);
        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        vrpBuilder.setRoutingCost(new GreatCircleCosts());
        VehicleRoutingProblem problem = vrpBuilder.build();

        /*
         * get the algorithm out-of-the-box.
         */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        algorithm.setMaxIterations(500);


        /*
         * and search a solution
         */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        /*
         * get the best
         */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

//        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

        new GraphStreamViewer(problem, Solutions.bestOf(solutions)).setRenderDelay(20).display();
        /*
         * plot
         */
//        new Plotter(problem,bestSolution).setLabel(Plotter.Label.ID).plot("output/plot", "mtw");

//        new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();
    }

}
