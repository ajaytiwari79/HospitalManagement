package com.kairos.shiftplanning.constraints.unitConstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Setter
@Getter
public class PreferedEmployementType  implements ConstraintHandler {
    private Set<Long> preferedEmploymentTypeIds;
    private ScoreLevel level;
    private int weight;

    public int checkConstraints(List<ShiftImp> shifts){
        int numberOfCasualEmployee = 0;
        for(ShiftImp  shift:shifts) {
            if (!this.preferedEmploymentTypeIds.contains(shift.getEmployee().getEmploymentTypeId())) {
                numberOfCasualEmployee++;
            }
        }
        return numberOfCasualEmployee;
    }
}
