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
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.algorithm.state.UpdateMaxTimeInVehicle;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.constraint.MaxTimeInVehicleConstraint;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.OSRMCosts;
import com.graphhopper.jsprit.core.util.Solutions;
import javafx.util.Pair;
import org.apache.logging.log4j.core.appender.routing.Route;
import scala.Int;
import scala.util.parsing.combinator.testing.Str;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class CureFitTest {
    static MongoDao mongoDao;
    static CentreConfigBean centreConfigBean;
    static RedisDao redisDao;
    static ArrayList<Integer> vehicleIdsAvailable;
    static String slot;
    static Double totalNumberOfRoutes = 0.0;
    static Double totalNumberOfShipments = 0.0;
    static Double totalNumberOfCarts = 0.0;


    public static void init(String slot, String centreName, String deliveryChannel, String deliveryDate) throws UnknownHostException, ParseException {
        centreConfigBean = CentreConfig.setCentreConfig(centreName, slot);
        mongoDao = new MongoDao(centreConfigBean.getCentreId(), slot, deliveryChannel, deliveryDate);
        redisDao = new RedisDao();
        new StaticUtil(centreConfigBean);
    }

    public static void runSolution(String dispatchTimeString) throws UnknownHostException, ParseException {

        Constants.DISPATCH_TIME_STRING = dispatchTimeString;
        Constants.DISPATCH_TIME = StaticUtil.getDateInEpoch(Constants.DISPATCH_TIME_STRING);
        StaticUtil.cartShipmentIdToCShipment.clear();

        ///////////////////////////////
        vehicleIdsAvailable = mongoDao.getAvailableVehicles(centreConfigBean.getCentreName(), slot);


        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
            .addCapacityDimension(Constants.WEIGHT_INDEX, Constants.CAPACITY_WEIGHT)
            .setCostPerWaitingTime(Constants.COST_PER_WAITING_TIME)
            .setFixedCost(Constants.FIXED_COST)
            .setCostPerDistance(Constants.COST_PER_DISTANCE);
        VehicleType vehicleType = vehicleTypeBuilder.build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for (Integer vehicleIds : vehicleIdsAvailable) {
            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleIds.toString());
            vehicleBuilder.setStartLocation(Location.newInstance(centreConfigBean.getCentreLong(), centreConfigBean.getCentreLat()));
            vehicleBuilder.setType(vehicleType).setReturnToDepot(true);
//            vehicleBuilder.setEarliestStart(0);
            VehicleImpl vehicle = vehicleBuilder.build();
            vrpBuilder.addVehicle(vehicle);

        }


        ArrayList<UserBean> userBeans = mongoDao.getUserData();
        for (UserBean userBean : userBeans) {
            if (StaticUtil.cartShipmentIdToCShipment.containsKey(userBean.getCartShipmentId())) {
                StaticUtil.cartShipmentIdToCShipment.get(userBean.getCartShipmentId()).addShipment(userBean.getShipmentId());
            } else {
                CartShipment cartShipment = new CartShipment(userBean.getUserId(), userBean.getOrderId(), userBean.getCartShipmentId(), userBean.getDeliveryStartTime(), userBean.getDeliveryEndTime(), userBean.getLatitude(), userBean.getLongitude(), userBean.getCreatedTime(), userBean.getDeliveryType(), userBean.getDeliverySlot(), userBean.getDeliveryStartTimeString(), userBean.getDeliveryendTimeString());
                cartShipment.addShipment(userBean.getShipmentId());
                StaticUtil.cartShipmentIdToCShipment.put(userBean.getCartShipmentId(), cartShipment);
            }
        }
        ArrayList<Shipment> shipments
            = new ArrayList<>();

        for (CartShipment cartShipment : StaticUtil.cartShipmentIdToCShipment.values()) {
            if (cartShipment.getDeliveryEndTime() >= Constants.DISPATCH_TIME) {
                if ((cartShipment.getDeliveryType().equals("SLOTTED") && Constants.cancelCutoffTimes.get(cartShipment.getDeliverySlot()) < Constants.DISPATCH_TIME) || cartShipment.getDeliveryType().equals("ON_DEMAND") && cartShipment.getDeliveryStartTime() <= Constants.DISPATCH_TIME) {
                    Shipment shipment = Shipment.Builder.newInstance(cartShipment.getCartShipmentId())
                        .setPickupLocation(Location.newInstance(centreConfigBean.getCentreLong(), centreConfigBean.getCentreLat()))
                        .setDeliveryLocation(Location.newInstance(cartShipment.getLongitude(), cartShipment.getLatitude()))
                        .setMaxTimeInVehicle(Constants.MAX_TIME_IN_VEHICLE)
                        .addDeliveryTimeWindow(Math.max(cartShipment.getDeliveryStartTime() - Constants.DISPATCH_TIME, 0), Math.max(cartShipment.getDeliveryEndTime() - Constants.DISPATCH_TIME, 0))
                        .addSizeDimension(Constants.WEIGHT_INDEX, cartShipment.getNumberOfShipments())
                        .setPriority(cartShipment.getDeliveryType().equals("SLOTTED") ? 1 : 10)
                        .build();
                    shipments.add(shipment);
                }

            }
        }
        if (vehicleIdsAvailable.size() == 0) {
            System.out.println("Pilots not available, UnassignedJobs: " + shipments.size());
            return;
        }


        for (Shipment shipment : shipments) {
            vrpBuilder.addJob(shipment);
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

        ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);
        constraintManager.addConstraint(new MaxTimeInVehicleConstraint(problem.getTransportCosts(), problem.getActivityCosts(), id, stateManager, problem, openJobsId), ConstraintManager.Priority.CRITICAL);


        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
            .setStateAndConstraintManager(stateManager, constraintManager).setProperty(Jsprit.Parameter.FAST_REGRET, "true")
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
        bestSolution = markJobsDone(bestSolution);


        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

//        new GraphStreamViewer(problem, Solutions.bestOf(solutions)).setRenderDelay(Constants.RENDER_DELAY).display();


//        try {
//            SerializerUtil.serializeObject(StaticUtil.distanceMatrix,"osrm_distances_" + centreConfigBean.getCentreName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        closeAllConnections();
        return;

    }

    public static void closeAllConnections() {
        redisDao.closeJedis();
        mongoDao.closeMongo();

    }

    public static VehicleRoutingProblemSolution markJobsDone(VehicleRoutingProblemSolution solution) throws ParseException {
        List<VehicleRoute> listOfRoutes = new ArrayList<VehicleRoute>(solution.getRoutes());

        Double totalWaitingTime, maxWaitingTime, totalLotWaitingTime, maxWaitingTimeInLot;
        String jobId;
        ArrayList<String> cartshipmentIds = new ArrayList<>();
        List<Job> jobsTobeUnassigned = new ArrayList<>();
        List<VehicleRoute> routesTobeRemoved = new ArrayList<>();
        List<Pair<Date, Integer>> vehicleDetails = new ArrayList<>();
        for (VehicleRoute route : listOfRoutes) {
            cartshipmentIds.clear();
            List<TourActivity> activities = route.getActivities();
            if (StaticUtil.checkStack(activities).equals(HardActivityConstraint.ConstraintsStatus.NOT_FULFILLED)) {
                System.out.println("StackCheck failed please check: ");
                for (TourActivity activity : activities) {
                    System.out.println(activity.getName() + ":" + ((TourActivity.JobActivity) activity).getJob().getId());
                    return null;
                }
            }
            boolean atleastOneDeliveredInLastTenMinutes = false;
            Integer numberOfShipments = 0;
            Integer numberOfCarts = 0;
            Integer numberOfLots = 0;
            Integer idx = 0;
            maxWaitingTime = 0.0;
            totalLotWaitingTime = 0.0;
            maxWaitingTimeInLot = 0.0;

            for (TourActivity activity : activities) {
                if (activity instanceof TourActivity.JobActivity) {
                    jobId = ((TourActivity.JobActivity) activity).getJob().getId();
                } else {
                    jobId = "-";
                }
                if (activity.getName().equals("pickupShipment") && idx > 0 && activities.get(idx + 1).getName().equals("deliverShipment")) {
                    maxWaitingTimeInLot = Math.max(maxWaitingTimeInLot, totalLotWaitingTime);
                    totalLotWaitingTime = 0.0;
                }
                maxWaitingTime = Math.max(maxWaitingTime, getWaitingTime(activity));
                totalLotWaitingTime += getWaitingTime(activity);

                if (activity.getName().equals("pickupShipment")) {
                    numberOfCarts++;
                    numberOfShipments += StaticUtil.cartShipmentIdToCShipment.get(jobId).getNumberOfShipments();
                    if (activities.get(idx + 1).getName().equals("deliverShipment")) {
                        numberOfLots++;
                    }
                }
                if (StaticUtil.cartShipmentIdToCShipment.containsKey(jobId)) {
                    cartshipmentIds.add(StaticUtil.cartShipmentIdToCShipment.get(jobId).getCartShipmentId());
                }
                idx++;
            }

            if (maxWaitingTime <= Constants.WAITING_TIME_THRESHOLD_PER_TRIP && maxWaitingTime <= Constants.INDIVIDUAL_WAITING_TIME_THRESHOLD) {

                totalNumberOfRoutes += numberOfLots;
                totalNumberOfCarts += numberOfCarts;
                totalNumberOfShipments += numberOfShipments;
                vehicleDetails.add(new Pair<>(StaticUtil.getDate(Constants.DISPATCH_TIME + Math.round(route.getEnd().getArrTime())), numberOfLots));
                for (String cartshipmentId : cartshipmentIds) {
                    mongoDao.setDoneJob(cartshipmentId);

                }

            } else {
                for (TourActivity activity : activities) {
                    jobsTobeUnassigned.add(((TourActivity.JobActivity) activity).getJob());
                }
                routesTobeRemoved.add(route);

            }
        }
        solution.removeRoutes(routesTobeRemoved);
        solution.addActivitiesToUnassigned(jobsTobeUnassigned);
        for (int i = 0; i < vehicleDetails.size(); i++) {
            mongoDao.setVehicleAvailibility(centreConfigBean.getCentreName(), slot, vehicleIdsAvailable.get(i), StaticUtil.getDate(Constants.DISPATCH_TIME), vehicleDetails.get(i).getKey(), vehicleDetails.get(i).getValue());
            System.out.println("VehicleId: " + vehicleIdsAvailable.get(i));

        }

        return solution;
    }

    public static Double getWaitingTime(TourActivity activity) {
        return activity.getEndTime() - activity.getArrTime();
    }

    public static boolean checkIfDeliveredInLastTenMinutes(TourActivity activity) {
        String jobId;
        if (activity instanceof TourActivity.JobActivity) {
            jobId = ((TourActivity.JobActivity) activity).getJob().getId();
        } else {
            jobId = "-";
        }
        Double timeInMinutes = activity.getName().equals("deliverShipment") ? Math.round((activity.getArrTime() - (StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryStartTime() - Constants.DISPATCH_TIME)) / 60) : 0.0;
        if (StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryType().equals("SLOTTED")) {
            return 45 - timeInMinutes <= 25;
        } else {
            return 60 - timeInMinutes <= 25;

        }


    }


    public static void main(String[] args) throws ParseException, UnknownHostException {
        String initialDispatchTimeString = "Tue Jul 03 11:20:00 IST 2018";
        slot = Constants.LUNCH_SLOT;
        Long epochTime = StaticUtil.getDateInEpoch(initialDispatchTimeString);
        init(Constants.LUNCH_SLOT, Constants.KITCHEN.HSR.name(), Constants.DELIVERY_CHANNEL, Constants.DELIVERY_DATE);
        mongoDao.setVehicleData(Constants.KITCHEN.HSR.name());
        mongoDao.resetFoodShipmentCollection();
        for (int i = 0; epochTime <= StaticUtil.getDateInEpoch("Tue Jul 03 15:00:00 IST 2018"); i++) {
            System.out.println(StaticUtil.getDate(epochTime).toString());
            runSolution(StaticUtil.getDate(epochTime).toString());
            epochTime += 180;
        }
        System.out.println("TotalNumOfRoutes: " + totalNumberOfRoutes);
        System.out.println("avgNumOfCarts: " + totalNumberOfCarts / totalNumberOfRoutes);
        System.out.println("avgNumOfShipments: " + totalNumberOfShipments / totalNumberOfRoutes);
        closeAllConnections();


    }
}
