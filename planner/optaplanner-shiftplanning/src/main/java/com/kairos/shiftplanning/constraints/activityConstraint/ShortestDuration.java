package com.kairos.shiftplanning.constraints.activityConstraint;

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
public class ShortestDuration implements Constraint {

    private int shortestDuration;
    private ScoreLevel level;
    private int weight;

    public ShortestDuration(int shortestDuration, ScoreLevel level, int weight) {
        this.shortestDuration = shortestDuration;
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, ShiftImp shift){
        return 0;
    }

    @Override
    public int checkConstraints(Activity activity, List<ShiftImp> shifts) {
        return 0;
    }
}
