package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoChangesToStaffWithCareBubble implements Constraint {
    private Long tagId;
    private ScoreLevel level;
    private int weight;
    List<Shift> shifts;

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return 0;
    }

    @Override
    public int checkConstraints(Activity activity, List<ShiftImp> shifts) {
        return 0;
    }
}
