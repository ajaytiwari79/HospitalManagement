package com.kairos.shiftplanning.constraints;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;

public interface Constraint extends ConstraintHandler {
    int checkConstraints(Activity activity, ShiftImp shift);
}
