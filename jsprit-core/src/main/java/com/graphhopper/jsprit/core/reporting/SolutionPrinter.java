/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.core.reporting;

import com.graphhopper.jsprit.core.CurefitUtil.Constants;
import com.graphhopper.jsprit.core.CurefitUtil.StaticUtil;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.DistanceUnit;
import com.graphhopper.jsprit.core.util.OSRMDistanceCalculator;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Printer to print the details of a vehicle-routing-problem solution.
 *
 * @author stefan schroeder
 */
public class SolutionPrinter implements Serializable {

    // Wrapping System.out into a PrintWriter
    static FileWriter fileWriter;

//    static {
//        try {
//            fileWriter = new FileWriter(String.format("osrm_outputs/%s", Constants.DISPATCH_TIME_STRING));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static PrintWriter SYSTEM_OUT_AS_PRINT_WRITER;

    /**
     * Enum to indicate verbose-level.
     * <p>
     * <p>
     * Print.CONCISE and Print.VERBOSE are available.
     *
     * @author stefan schroeder
     */
    public enum Print {

        CONCISE, VERBOSE
    }

    private static class Jobs {
        int nServices;
        int nShipments;
        int nBreaks;

        public Jobs(int nServices, int nShipments, int nBreaks) {
            super();
            this.nServices = nServices;
            this.nShipments = nShipments;
            this.nBreaks = nBreaks;
        }
    }


    /**
     * Prints costs and #vehicles to stdout (out.println).
     *
     * @param solution the solution to be printed
     */
    public static void print(VehicleRoutingProblemSolution solution) {
        print(SYSTEM_OUT_AS_PRINT_WRITER, solution);
        SYSTEM_OUT_AS_PRINT_WRITER.flush();
    }

    /**
     * Prints costs and #vehicles to the given writer
     *
     * @param out      the destination writer
     * @param solution the solution to be printed
     */
    public static void print(PrintWriter out, VehicleRoutingProblemSolution solution) {
        out.println("[costs=" + solution.getCost() + "]");
        out.println("[#vehicles=" + solution.getRoutes().size() + "]");
    }

    /**
     * Prints costs and #vehicles to the to stdout (out.println).
     *
     * @param solution the solution to be printed
     */
    public static void print(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution, Print print) {
        try {
            fileWriter = new FileWriter(String.format("osrm_outputs/%s", Constants.DISPATCH_TIME_STRING));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SYSTEM_OUT_AS_PRINT_WRITER = new PrintWriter(fileWriter);
        print(SYSTEM_OUT_AS_PRINT_WRITER, problem, solution, print);
        SYSTEM_OUT_AS_PRINT_WRITER.flush();
        SYSTEM_OUT_AS_PRINT_WRITER.close();
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Prints costs and #vehicles to the given writer
     *
     * @param out      the destination writer
     * @param solution the solution to be printed
     */
    public static void print(PrintWriter out, VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution, Print print) {
        String leftAlign = "| %-13s | %-8s | %n";

        out.format("+--------------------------+%n");
        out.printf("| problem                  |%n");
        out.format("+---------------+----------+%n");
        out.printf("| indicator     | value    |%n");
        out.format("+---------------+----------+%n");

        out.format(leftAlign, "noJobs", problem.getJobs().values().size());
        Jobs jobs = getNuOfJobs(problem);
        out.format(leftAlign, "noServices", jobs.nServices);
        out.format(leftAlign, "noShipments", jobs.nShipments);
        out.format(leftAlign, "noBreaks", jobs.nBreaks);
        out.format(leftAlign, "fleetsize", problem.getFleetSize().toString());
        out.format("+--------------------------+%n");


        String leftAlignSolution = "| %-13s | %-40s | %n";
        out.format("+----------------------------------------------------------+%n");
        out.printf("| solution                                                 |%n");
        out.format("+---------------+------------------------------------------+%n");
        out.printf("| indicator     | value                                    |%n");
        out.format("+---------------+------------------------------------------+%n");
        out.format(leftAlignSolution, "costs", solution.getCost());
        out.format(leftAlignSolution, "noVehicles", solution.getRoutes().size());
        out.format(leftAlignSolution, "unassgndJobs", solution.getUnassignedJobs().size());
        out.format("+----------------------------------------------------------+%n");

        if (print.equals(Print.VERBOSE)) {
            printVerbose(out, problem, solution);
        }

        System.out.println("Object has been serialized");
    }

    private static void printVerbose(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution) {
        printVerbose(SYSTEM_OUT_AS_PRINT_WRITER, problem, solution);
        SYSTEM_OUT_AS_PRINT_WRITER.flush();
        SYSTEM_OUT_AS_PRINT_WRITER.close();
    }

    private static void printVerbose(PrintWriter out, VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution) {
        String leftAlgin = "| %-7s | %-20s | %-21s | %-15s | %-15s | %-15s | %-15s | %-15s | %-15s | %-15s |%n";
        out.format("+--------------------------------------------------------------------------------------------------------------------------------------------------------------------+-----------------+%n");
        out.printf("| detailed solution                                                                                                                                |                 |                  %n");
        out.format("+---------+----------------------+-----------------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+------------------%n");
        out.printf("| route   | vehicle              | activity              | job             | arrTime         | endTime        | waitTime(min)   | DeliveryType    |Time after start(Min)| Distance From kitchen %n");
        int routeNu = 1;

        List<VehicleRoute> list = new ArrayList<VehicleRoute>(solution.getRoutes());
        Collections.sort(list , new com.graphhopper.jsprit.core.util.VehicleIndexComparator());
        Double totalPickups =0.0;
        Double totalShipmentsPickedUpInRoute = 0.0;
        Double totalNumberOfRoutes =0.0;
        Double totalWaitingTime = 0.0;
        Double onDemandTotalArrivalTimeAfterStart =0.0;
        Double slottedTotalArrivalTimeAfterStart =0.0;
        Double totalSlottedOrders =0.0;
        Double totalOnDemandOrders =0.0;








        for (VehicleRoute route : list) {
            out.format("+---------+----------------------+-----------------------+-----------------+-----------------+----------------+-----------------+-----------------+-----------------+-----------------+%n");
            double costs = 0;
            out.format(leftAlgin, routeNu, getVehicleString(route), route.getStart().getName(), "-", "undef", Math.round(route.getStart().getEndTime()),
                Math.round(costs),0,"","-","-");
            TourActivity prevAct = route.getStart();
            int idx = 0;
            List<TourActivity> activities = route.getActivities();
            for (TourActivity act : activities) {
                String jobId;
                if (act instanceof TourActivity.JobActivity) {
                    jobId = ((TourActivity.JobActivity) act).getJob().getId();
                } else {
                    jobId = "-";
                }
                double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(), prevAct.getEndTime(), route.getDriver(),
                    route.getVehicle());
                c += problem.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(), route.getVehicle());
                costs += c;
                Double waitingTime = act.getEndTime() -  act.getArrTime();
                totalWaitingTime+= waitingTime;
                totalPickups += act.getName().equals("pickupShipment")?1.0:0.0;
                totalShipmentsPickedUpInRoute += StaticUtil.cartShipmentIdToCShipment.get(jobId).getNumberOfShipments();
                out.format(leftAlgin,
                    routeNu,
                    getVehicleString(route),
                    act.getName(),
                    jobId,
                    Math.round(act.getArrTime()),
                    Math.round(act.getEndTime()),
                    Math.round(waitingTime/60),
                    act.getName().equals("deliverShipment")?StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryType():"",
                    act.getName().equals("deliverShipment")? Math.round((act.getArrTime()-(StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryStartTime()-Constants.DISPATCH_TIME))/60):" ",
                    act.getName().equals("deliverShipment")?OSRMDistanceCalculator.calculateDistance(StaticUtil.centreConfigBean.getCentreCoordinate(),StaticUtil.cartShipmentIdToCShipment.get(jobId).getCoordinate(),DistanceUnit.Meter):"-");
                prevAct = act;
                if(act.getName().equals("pickupShipment")&&activities.get(idx+1).getName().equals("deliverShipment"))
                {
                    totalNumberOfRoutes++;
                }
                if(act.getName().equals("deliverShipment"))
                {
                    if(StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryType().equals("ON_DEMAND"))
                    {
                        onDemandTotalArrivalTimeAfterStart+=Math.round((act.getArrTime()-(StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryStartTime()-Constants.DISPATCH_TIME))/60);
                        totalOnDemandOrders++;
                    }
                    else if(StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryType().equals("SLOTTED"))
                    {
                        slottedTotalArrivalTimeAfterStart+=Math.round((act.getArrTime()-(StaticUtil.cartShipmentIdToCShipment.get(jobId).getDeliveryStartTime()-Constants.DISPATCH_TIME))/60);
                        totalSlottedOrders++;
                    }
                }
                idx++;
            }
            double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(), prevAct.getEndTime(),
                route.getDriver(), route.getVehicle());
            c += problem.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(), route.getVehicle());
            costs += c;
            out.format(leftAlgin, routeNu, getVehicleString(route), route.getEnd().getName(), "-", Math.round(route.getEnd().getArrTime()), "undef",
                0,"-","-","-");
            routeNu++;
        }
        out.format("+--------------------------------------------------------------------------------------------------------------------------------+%n");
        if (!solution.getUnassignedJobs().isEmpty()) {
            out.format("+----------------+----------------+-------------+------------------------------+------------------------------+%n");
            out.format("| unassignedJobs |Delivery Start Time           |Delivery End   Time           |Distance           |%n");
            out.format("+----------------+----------------+-------------+------------------------------+%n");
            String unassignedJobAlgin = "| %-14s | %-14s | %-14s | %-14s |%n";
            for (Job j : solution.getUnassignedJobs()) {
                out.format(unassignedJobAlgin, j.getId(),StaticUtil.cartShipmentIdToCShipment.get(j.getId()).getDeliveryStartTimeString(),StaticUtil.cartShipmentIdToCShipment.get(j.getId()).getDeliveryendTimeString(),OSRMDistanceCalculator.calculateDistance(StaticUtil.centreConfigBean.getCentreCoordinate(),StaticUtil.cartShipmentIdToCShipment.get(j.getId()).getCoordinate(),DistanceUnit.Meter));
            }
            out.format("+----------------+%n");
        }

        out.format("+----------------+%n");
        out.format("| avgNumOfCartsPerTrip |%n");
        out.format("+----------------+%n");
        String avgNumCartsString = "| %.2f |%n";
        out.format(avgNumCartsString, totalPickups/totalNumberOfRoutes);
        out.format("| avgShipmentsPerTrip |%n");
        out.format("+----------------+%n");
        String avgNumShipmentsString = "| %.2f |%n";
        out.format(avgNumShipmentsString, totalShipmentsPickedUpInRoute/totalNumberOfRoutes);

        out.format("+----------------+%n");
        out.format("|TotalWaitingTime(Min)|%n");
        out.format("+----------------+%n");
        String totalWaitingTimeString = "| %.2f |%n";
        out.format(totalWaitingTimeString, totalWaitingTime/60);

        out.format("+----------------+%n");
        out.format("|AvgWaitingTimePerTrip(Min)|%n");
        out.format("+----------------+%n");
        String avgWaitingTimeString = "| %.2f |%n";
        out.format(avgWaitingTimeString, totalWaitingTime/(60*totalNumberOfRoutes));

        out.format("+----------------+%n");
        out.format("|On Demand Avg Arrival Time after Start(Min)|%n");
        out.format("+----------------+%n");
        String onDemandAvgArrivalTimeString = "| %.2f |%n";
        out.format(onDemandAvgArrivalTimeString, onDemandTotalArrivalTimeAfterStart/totalOnDemandOrders);

        out.format("+----------------+%n");
        out.format("|Slotted Avg Arrival Time after Start(Min)|%n");
        out.format("+----------------+%n");
        String slottedAvgArrivalTimeString = "| %.2f |%n";
        out.format(slottedAvgArrivalTimeString, slottedTotalArrivalTimeAfterStart/totalSlottedOrders);


    }

    private static String getVehicleString(VehicleRoute route) {
        return route.getVehicle().getId();
    }

    private static Jobs getNuOfJobs(VehicleRoutingProblem problem) {
        int nShipments = 0;
        int nServices = 0;
        int nBreaks = 0;
        for (Job j : problem.getJobs().values()) {
            if (j instanceof Shipment) {
                nShipments++;
            }
            if (j instanceof Service) {
                nServices++;
            }
            if (j instanceof Break) {
                nBreaks++;
            }
        }
        return new Jobs(nServices, nShipments, nBreaks);
    }

}
