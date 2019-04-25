package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;

import java.util.HashSet;
import java.util.Set;

public class MaxDiffrentActivity implements ConstraintHandler {

    //In minutes
    private int maxDiffrentActivity;

    public MaxDiffrentActivity() {
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

    public MaxDiffrentActivity(int maxDiffrentActivity, ScoreLevel level, int weight) {
        this.maxDiffrentActivity = maxDiffrentActivity;
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(ShiftImp shift){
        Set<Activity> activities = new HashSet<>();
        for (ActivityLineInterval ali:shift.getActivityLineIntervals()) {
             activities.add(ali.getActivity());
        }
        return activities.size()>maxDiffrentActivity?activities.size()-maxDiffrentActivity:0;
    }


}
