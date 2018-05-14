package com.kairos.shiftplanning.domain.activityConstraint;

import com.kairos.shiftplanning.domain.ActivityPlannerEntity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;

import java.util.List;

public class MaxAllocationPerShift implements ConstraintHandler {

    private int maxAllocationPerShift;

    public MaxAllocationPerShift() {
    }

    @Override
    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    private ScoreLevel level;
    private int weight;

    public MaxAllocationPerShift(int maxAllocationPerShift, ScoreLevel level, int weight) {
        this.maxAllocationPerShift = maxAllocationPerShift;
        this.level = level;
        this.weight = weight;
    }


    public int checkConstraints(ActivityPlannerEntity activityPlannerEntity, ShiftRequestPhase shift){
        List<ActivityLineInterval> alis = shift.getActivityLineIntervals();
        ShiftPlanningUtility.sortActivityLineIntervals(alis);
        int allocatedActivityCount = 0;
        /*for (int i=1;i<alis.size();i++){
            if(alis.get(i-1).getActivityPlannerEntity().equals(activityPlannerEntity) && !alis.get(i).getActivityPlannerEntity().equals(activityPlannerEntity)){
                allocatedActivityCount++;
            }
        }
        if(alis.get(alis.size()-1).getActivityPlannerEntity().equals(activityPlannerEntity)){
            allocatedActivityCount++;
        }*/
        ActivityLineInterval prev=null;
        for(ActivityLineInterval ali:alis){
            if(ali.getActivityPlannerEntity().equals(activityPlannerEntity) && !ali.getActivityPlannerEntity().equals(prev==null?null:prev.getActivityPlannerEntity())){
                allocatedActivityCount++;
            }
            prev=ali;
        }
        return allocatedActivityCount > maxAllocationPerShift?allocatedActivityCount-maxAllocationPerShift:0;
    }
}
