package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LongestDuration implements Constraint {


    //By percent
    private int longestDuration;
    private ScoreLevel level;
    private int weight;

    public LongestDuration(int longestDuration, ScoreLevel level, int weight) {
        this.longestDuration = longestDuration;
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, ShiftImp shift){

        return 0;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts){
        return 0;
    }
}
