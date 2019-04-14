package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.constraints.ScoreLevel;

public class ContinousActivityPerShift {

    private int contActivityPerShift;
    private int weight;
    private ScoreLevel level;

    public ContinousActivityPerShift(int contActivityPerShift, ScoreLevel level, int weight) {
        this.contActivityPerShift = contActivityPerShift;
        this.weight = weight;
        this.level = level;
    }

    public int getContActivityPerShift() {
        return contActivityPerShift;
    }

    public void setContActivityPerShift(int contActivityPerShift) {
        this.contActivityPerShift = contActivityPerShift;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    public boolean checkConstraints(ActivityLineInterval lineInterval){
        boolean isValid = false;
        /*int count = 0;
        List<ActivityLineInterval> aLIs = (List<ActivityLineInterval>) lineInterval.getShift().getActivityLineIntervalsList().stream().sorted(ShiftPlanningUtility.getActivityIntervalStartTimeComparator()).collect(Collectors.toList());
        if(aLIs.contains(lineInterval)) {
            aLIs = aLIs.subList(aLIs.indexOf(lineInterval),aLIs.size());
            //TODO need to care of all occurance of this activity
            for (ActivityLineInterval ali : aLIs) {
                if (ali.getActivity().equals(lineInterval.getActivity())) {
                    count++;
                } else break;
            }
            isValid = count<contActivityPerShift?true:false;
        }*/
        return isValid;
    }
}
