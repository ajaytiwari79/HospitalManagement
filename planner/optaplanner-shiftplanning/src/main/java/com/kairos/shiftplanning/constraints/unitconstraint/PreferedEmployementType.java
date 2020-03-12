package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Setter
@Getter
public class PreferedEmployementType  implements Constraint {
    private Set<Long> preferedEmploymentTypeIds;
    private ScoreLevel level;
    private int weight;

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return 0;
    }

    @Override
    public <T extends Constraint> int checkConstraints(T t, List<ShiftImp> shifts) {
        int numberOfCasualEmployee = 0;
        for(ShiftImp  shift:shifts) {
            if (!this.preferedEmploymentTypeIds.contains(shift.getEmployee().getEmploymentTypeId())) {
                numberOfCasualEmployee++;
            }
        }
        return numberOfCasualEmployee;
    }
}
