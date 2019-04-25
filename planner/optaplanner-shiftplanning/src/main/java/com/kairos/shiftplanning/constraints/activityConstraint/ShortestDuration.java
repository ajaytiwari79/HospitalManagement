package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;

public class ShortestDuration {

    private int shortestDuration;

    public ShortestDuration() {
    }

    private ScoreLevel level;
    private int weight;

    public ShortestDuration(int shortestDuration, ScoreLevel level, int weight) {
        this.shortestDuration = shortestDuration;
        this.level = level;
        this.weight = weight;
    }

    public int getShortestDuration() {
        return shortestDuration;
    }

    public void setShortestDuration(int shortestDuration) {
        this.shortestDuration = shortestDuration;
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
       /*int minutes = shift.getActivityLineIntervalsList().stream().filter(a->a.getActivity().equals(activity)).mapToInt(a->a.getInterval().toDuration().toStandardMinutes().getMinutes()).sum();
        if(minutes>0){
            int duration = Math.round((float)minutes/shift.getMinutes()*100);
            return duration<shortestDuration?shortestDuration-duration:0;
        }*/
        return 0;
    }
}
