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
package com.graphhopper.jsprit.core.problem.constraint;

import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.graphhopper.jsprit.core.CurefitUtil.StaticUtil.checkStack;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ServiceDeliveriesFirstConstraint implements HardActivityConstraint {

    @Override
    public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
        if (newAct instanceof PickupService && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof ServiceActivity && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof DeliverService && prevAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof DeliverService && prevAct instanceof ServiceActivity) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }

        if (newAct instanceof DeliverService && prevAct instanceof PickupShipment) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof DeliverService && prevAct instanceof DeliverShipment) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof PickupShipment && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof DeliverShipment && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if(newAct instanceof PickupShipment && prevAct instanceof DeliverShipment && nextAct instanceof DeliverShipment ) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if(newAct instanceof DeliverShipment && prevAct instanceof PickupShipment && nextAct instanceof PickupShipment ) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if(newAct instanceof DeliverShipment)
        {
            List<TourActivity> tourActivityList = iFacts.getRoute().getActivities();
            Integer pickupIdx = iFacts.getRelatedActivityContext().getInsertionIndex();
            Integer deliverIndex = iFacts.getActivityContext().getInsertionIndex();

            int n = tourActivityList.size(),sum=0;
            Boolean closeFlagEnabled= false;
            for(int i =0 ;i<n;i++)
            {
                if(i == pickupIdx)
                {
                    sum+=1;
                }
                else if(i == deliverIndex)
                {
                    sum-=1;
                    closeFlagEnabled =true;
                }
                if(closeFlagEnabled)
                {
                    if(tourActivityList.get(i) instanceof PickupShipment)
                        return ConstraintsStatus.NOT_FULFILLED;
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
                if(sum == 0)
                    closeFlagEnabled=false;
                else if(sum < 0)
                    return ConstraintsStatus.NOT_FULFILLED;

            }
            if(sum<0)
                return ConstraintsStatus.NOT_FULFILLED;
        }


        return ConstraintsStatus.FULFILLED;
    }



    public static void main(String[] args) {
        List<TourActivity> tourActivities = new ArrayList<>();
        tourActivities.add(mock(PickupShipment.class));
        tourActivities.add(mock(PickupShipment.class));
        tourActivities.add(mock(PickupShipment.class));
        tourActivities.add(mock(DeliverShipment.class));
        tourActivities.add(mock(DeliverShipment.class));
        tourActivities.add(mock(PickupShipment.class));
        tourActivities.add(mock(DeliverShipment.class));
        tourActivities.add(mock(DeliverShipment.class));

        System.out.println(checkStack(tourActivities));

    }

}
