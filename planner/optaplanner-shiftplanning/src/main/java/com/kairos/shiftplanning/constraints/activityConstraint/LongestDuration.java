package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;

public class LongestDuration implements ConstraintHandler {


    //By percent
    private int longestDuration;
    private ScoreLevel level;
    private int weight;

    public LongestDuration(int longestDuration, ScoreLevel level, int weight) {
        this.longestDuration = longestDuration;
        this.level = level;
        this.weight = weight;
    }

    public LongestDuration() {
    }

    public int getLongestDuration() {
        return longestDuration;
    }

    public void setLongestDuration(int longestDuration) {
        this.longestDuration = longestDuration;
    }

    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, ShiftImp shift){

        return 0;
    }
}
