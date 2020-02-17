package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class MaxDiffrentActivity implements Constraint {

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
}
