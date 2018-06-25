package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.core.Bean.CartShipment;
import com.graphhopper.jsprit.core.Bean.CentreConfigBean;
import com.graphhopper.jsprit.core.Bean.UserBean;
import com.graphhopper.jsprit.core.CurefitUtil.CentreConfig;
import com.graphhopper.jsprit.core.CurefitUtil.Constants;
import com.graphhopper.jsprit.core.CurefitUtil.StaticUtil;
import com.graphhopper.jsprit.core.DAL.MongoDao;
import com.graphhopper.jsprit.core.DAL.RedisDao;
import com.graphhopper.jsprit.core.DAL.SerializerUtil;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.algorithm.state.UpdateMaxTimeInVehicle;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.MaxTimeInVehicleConstraint;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.GreatCircleCosts;
import com.graphhopper.jsprit.core.util.OSRMCosts;
import com.graphhopper.jsprit.core.util.OSRMDistanceCalculator;
import com.graphhopper.jsprit.core.util.Solutions;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CureFitTest {
    static MongoDao mongoDao;
    static CentreConfigBean centreConfigBean;
    static RedisDao redisDao;


    public static void init(String slot, String centreName, String deliveryChannel, String deliveryDate) throws UnknownHostException {
        centreConfigBean = CentreConfig.setCentreConfig(centreName, slot);
        mongoDao = new MongoDao(centreConfigBean.getCentreId(), slot, deliveryChannel, deliveryDate);
        redisDao = new RedisDao();
        new StaticUtil(centreConfigBean.getCentreName());

    }

    public static void main(String[] args) throws UnknownHostException {




        init(Constants.LUNCH_SLOT,Constants.KITCHEN.HSR.name(),Constants.DELIVERY_CHANNEL, Constants.DELIVERY_DATE );

        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
            .addCapacityDimension(Constants.WEIGHT_INDEX, Constants.CAPACITY_WEIGHT)
            .setCostPerWaitingTime(2)
            .setFixedCost(200000);
        VehicleType vehicleType = vehicleTypeBuilder.build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for( int i=0;i<centreConfigBean.getFleetSize(); i++)
        {
            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("v: " + i);
            vehicleBuilder.setStartLocation(Location.newInstance(centreConfigBean.getCentreLong(), centreConfigBean.getCentreLat()));
            vehicleBuilder.setType(vehicleType);
            //setting earliest time to 0
            vehicleBuilder.setEarliestStart(0);
            VehicleImpl vehicle = vehicleBuilder.build();
            vrpBuilder.addVehicle(vehicle);

        }


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
                CartShipment cartShipment = new CartShipment(userBean.getUserId(), userBean.getOrderId(), userBean.getCartShipmentId(), userBean.getDeliveryStartTime(), userBean.getDeliveryEndTime(), userBean.getLatitude(), userBean.getLongitude(), userBean.getCreatedTime(),userBean.getDeliveryType());
                cartShipment.addShipment(userBean.getShipmentId());
                cartShipmentIdToCShipment.put(userBean.getCartShipmentId(), cartShipment);
            }
        }
        ArrayList<Shipment> shipments
            = new ArrayList<>();

        for (CartShipment cartShipment :cartShipmentIdToCShipment.values())
        {
                Shipment shipment = Shipment.Builder.newInstance(cartShipment.getCartShipmentId())
                    .setPickupLocation(Location.newInstance(centreConfigBean.getCentreLong(), centreConfigBean.getCentreLat()))
                    .setDeliveryLocation(Location.newInstance(cartShipment.getLongitude(), cartShipment.getLatitude()))
                    .setDeliveryServiceTime(Constants.DELIVERY_SERVICE_TIME)
                    .setMaxTimeInVehicle(Constants.MAX_TIME_IN_VEHICLE)
                    .addDeliveryTimeWindow(Math.max(cartShipment.getDeliveryStartTime() - Constants.DISPATCH_TIME,0),Math.max(cartShipment.getDeliveryEndTime() - Constants.DISPATCH_TIME,0))
//                    .addDeliveryTimeWindow((cartShipment.getDeliveryStartTime()),(cartShipment.getDeliveryEndTime()))
                    .addSizeDimension(Constants.WEIGHT_INDEX, cartShipment.getNumberOfShipments())
                    //get the avg time to deliver a slotted order (minimise this constraint)
                    .setPriority(cartShipment.getDeliveryType().equals("SLOTTED")?1:2)
                    .build();
                shipments.add(shipment);

                if( cartShipment.getDeliveryStartTime() < Constants.DISPATCH_TIME)
                    System.out.println(cartShipment.getDeliveryStartTime()+":"+Constants.DISPATCH_TIME);
                if( cartShipment.getDeliveryEndTime() < Constants.DISPATCH_TIME)
                    System.out.println(cartShipment.getDeliveryEndTime()+":"+Constants.DISPATCH_TIME);


        }




        int i=0;
        for(Shipment shipment : shipments)
        {
            vrpBuilder.addJob(shipment);
//            if(i==5)
//                break;
//            i++;
        }

        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        vrpBuilder.setRoutingCost(new OSRMCosts());
//        vrpBuilder.setRoutingCost(new GreatCircleCosts());
        VehicleRoutingProblem problem = vrpBuilder.build();

        /*
         * get the algorithm out-of-the-box.
         */

        StateManager stateManager = new StateManager(problem);
        StateId id = stateManager.createStateId("max-time");
        StateId openJobsId = stateManager.createStateId("open-jobs-id");
        stateManager.addStateUpdater(new UpdateMaxTimeInVehicle(stateManager, id, problem.getTransportCosts(), problem.getActivityCosts(), openJobsId));

        ConstraintManager constraintManager = new ConstraintManager(problem,stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);
        constraintManager.addConstraint(new MaxTimeInVehicleConstraint(problem.getTransportCosts(), problem.getActivityCosts(), id, stateManager, problem, openJobsId), ConstraintManager.Priority.CRITICAL);



        //check fleetmanager example

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
            .setStateAndConstraintManager(stateManager,constraintManager).setProperty(Jsprit.Parameter.FAST_REGRET, "true")
            .setProperty(Jsprit.Parameter.THREADS, Constants.NUM_OF_THREADS)
            .setProperty(Jsprit.Parameter.FIXED_COST_PARAM, "2.")
            .buildAlgorithm();

        algorithm.setMaxIterations(Constants.MAX_ITERATIONS);



        /*
         * and search a solution
         */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        /*
         * get the best
         */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);


        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

        new GraphStreamViewer(problem, Solutions.bestOf(solutions)).setRenderDelay(Constants.RENDER_DELAY).display();

        redisDao.closeJedis();

//        try {
//            SerializerUtil.serializeObject(StaticUtil.distanceMatrix,"osrm_distances_" + centreConfigBean.getCentreName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


    }


}
