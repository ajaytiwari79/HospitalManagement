package com.kairos.shiftplanning.constraints.unitConstraint;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;

import java.util.List;

public class PreferedEmployementType  implements ConstraintHandler {
    private Long employmentTypeId;
    private ScoreLevel level;
    private int weight;


    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    @Override
    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int checkConstraints(List<ShiftImp> shifts){
        int numberOfCasualEmployee = 0;
        for(ShiftImp  shift:shifts) {
            if (shift.getEmployee().getEmploymentTypeId() == this.employmentTypeId) {
                continue ;
            } else {
                numberOfCasualEmployee++;
            }
        }
        return numberOfCasualEmployee;
    }
}
