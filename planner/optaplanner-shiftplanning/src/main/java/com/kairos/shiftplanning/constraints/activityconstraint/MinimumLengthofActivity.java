package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MinimumLengthofActivity implements ConstraintHandler {

    //In minutes
    private int minimumLengthofActivity;
    private ScoreLevel level;
    private int weight;

    public MinimumLengthofActivity(int minimumLengthofActivity, ScoreLevel level, int weight) {
        this.minimumLengthofActivity = minimumLengthofActivity;
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, ShiftImp shift){
        List<ActivityLineInterval> alis = shift.getActivityLineIntervals();
        ShiftPlanningUtility.sortActivityLineIntervals(alis);
        int contMins=0;
        int totalDiff=0;
        for(ActivityLineInterval ali:alis){
            if(ali.getActivity().equals(activity)){
                contMins+=ali.getDuration();
            }else if(contMins>0){
                totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
                contMins=0;
            }
        }
        //for last activity
        if(contMins>0){
            totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
        }
        return totalDiff/15;
    }

    @Override
    public int verifyConstraints(Activity activity, Shift shift){
        List<ALI> alis = shift.getActivityLineIntervals();
        StaffingLevelPlanningUtility.sortActivityLineIntervals(alis);
        int contMins=0;
        int totalDiff=0;
        for(ALI ali:alis){
            if(ali.getActivity().equals(activity)){
                contMins+=ali.getDuration();
            }else if(contMins>0){
                totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
                contMins=0;
            }
        }
        //for last activity
        if(contMins>0){
            totalDiff+=minimumLengthofActivity>contMins?minimumLengthofActivity-contMins:0;
        }
        return totalDiff/15;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }

    @Override
    public int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps){return 0;};

}
