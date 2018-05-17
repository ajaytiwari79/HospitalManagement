package com.kairos.shiftplanning.domain.activityConstraint;

import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;

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

    public int checkConstraints(Activity activity, ShiftRequestPhase shift){

        return 0;
    }
}
