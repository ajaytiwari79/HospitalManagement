package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MaxDiffrentActivity implements ConstraintHandler {

    private int maxDiffrentActivity;
    private ScoreLevel level;
    private int weight;

    public MaxDiffrentActivity(int maxDiffrentActivity, ScoreLevel level, int weight) {
        this.maxDiffrentActivity = maxDiffrentActivity;
        this.level = level;
        this.weight = weight;
    }


    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        Set<Activity> activities = new HashSet<>();
        for (ActivityLineInterval ali:shift.getActivityLineIntervals()) {
            activities.add(ali.getActivity());
        }
        return activities.size()>maxDiffrentActivity?activities.size()-maxDiffrentActivity:0;
    }

    @Override
    public int verifyConstraints(Activity activity, Shift shift) {
        Set<Activity> activities = new HashSet<>();
        for (ALI ali:shift.getActivityLineIntervals()) {
            activities.add(ali.getActivity());
        }
        return activities.size()>maxDiffrentActivity?activities.size()-maxDiffrentActivity:0;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }

    @Override
    public int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps){return 0;};
}
