package com.kairos.shiftplanning.domain.activityConstraint;

import com.kairos.shiftplanning.domain.ActivityPlannerEntity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;

import java.util.List;

public class MinimumLengthofActivity implements ConstraintHandler {

    //In minutes
    private int minimumLengthofActivity;
    private ScoreLevel level;
    private int weight;

    public MinimumLengthofActivity() {
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
    public MinimumLengthofActivity(int minimumLengthofActivity, ScoreLevel level, int weight) {
        this.minimumLengthofActivity = minimumLengthofActivity;
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(ActivityPlannerEntity activityPlannerEntity, ShiftRequestPhase shift){
        List<ActivityLineInterval> alis = shift.getActivityLineIntervals();
        ShiftPlanningUtility.sortActivityLineIntervals(alis);
        int contMins=0;
        int totalDiff=0;
        for(ActivityLineInterval ali:alis){
            if(ali.getActivityPlannerEntity().equals(activityPlannerEntity)){
                contMins+=ali.getDuration();
            }else if(contMins>0){
                totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
                contMins=0;
            }
        }
        //for last activityPlannerEntity
        if(contMins>0){
            totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
        }
        return totalDiff/15;
    }
    //get first index of where activity of Interval is equal to activity
       /* int firstOccuredIndex = IntStream.range(0,shift.getActivityLineIntervalsList().size()).filter(i->shift.getActivityLineIntervalsList().get(i).getActivityPlannerEntity().equals(activity)).findFirst().getAsInt();
        List<ActivityLineInterval> activityLineIntervals = shift.getActivityLineIntervalsList().subList(firstOccuredIndex,shift.getActivityLineIntervalsList().size());
        boolean updateInterval = false;
        Interval totalInterval = activityLineIntervals.get(0).getInterval();
        for (int i=1;i<activityLineIntervals.size();i++) {
            if(updateInterval && activityLineIntervals.get(i-1).getActivityPlannerEntity().equals(activity)){
                updateInterval = false;
                totalInterval = activityLineIntervals.get(i-1).getInterval();
            }else if(updateInterval){
                continue;
            }
            if(activityLineIntervals.get(i-1).getActivityPlannerEntity().equals(activity) && activityLineIntervals.get(i).getActivityPlannerEntity().equals(activity)){
                totalInterval = totalInterval.withEnd(activityLineIntervals.get(i).getEnd());
            }
            else {
                if(totalInterval.toDuration().getStandardMinutes()<minimumLengthofActivity){
                    return minimumLengthofActivity - (int) totalInterval.toDuration().getStandardMinutes();
                }else {
                    updateInterval = true;
                    continue;
                }
            }
        }
        return totalInterval.toDuration().getStandardMinutes()<minimumLengthofActivity?minimumLengthofActivity-(int)totalInterval.toDuration().getStandardMinutes():0;
*/

}
