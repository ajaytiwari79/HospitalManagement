package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.isValidForDayType;

/**
 * @author pradeep
 * @date - 18/12/18
 */
@Getter
@Setter
@NoArgsConstructor
public class ActivityDayType implements Constraint {

    private List<DayType> dayTypes;
    private ScoreLevel level;
    private int weight;


    public ActivityDayType(List<DayType> dayTypes, ScoreLevel level, int weight) {
        this.dayTypes = dayTypes;
        this.level = level;
        this.weight = weight;
    }


    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return isValidForDayType(shift,this.dayTypes) ? 0 : 1;
    }
}
