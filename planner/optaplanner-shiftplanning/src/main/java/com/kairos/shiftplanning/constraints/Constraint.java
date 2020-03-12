package com.kairos.shiftplanning.constraints;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;

import java.util.List;

public interface Constraint extends ConstraintHandler {
    int checkConstraints(Activity activity, ShiftImp shift);
    <T extends Constraint> int checkConstraints(T t, List<ShiftImp> shifts);

}
