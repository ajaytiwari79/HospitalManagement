package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
public class PreferedEmployementType  implements Constraint {
    private Set<Long> preferedEmploymentTypeIds;
    private ScoreLevel level;
    private int weight;

    public PreferedEmployementType(Set<Long> preferedEmploymentTypeIds, ScoreLevel level, int weight) {
        this.preferedEmploymentTypeIds = preferedEmploymentTypeIds;
        this.level = level;
        this.weight = weight;
    }

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        return 0;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        int numberOfCasualEmployee = 0;
        for(ShiftImp  shift:shifts) {
            if (!this.preferedEmploymentTypeIds.contains(shift.getEmployee().getEmploymentTypeId())) {
                numberOfCasualEmployee++;
            }
        }
        return numberOfCasualEmployee;
    }
}
