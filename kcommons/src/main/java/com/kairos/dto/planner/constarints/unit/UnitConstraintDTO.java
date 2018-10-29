package com.kairos.dto.planner.constarints.unit;

import com.kairos.dto.planner.constarints.ConstraintDTO;

public class UnitConstraintDTO extends ConstraintDTO {
    //~
    private Long unitId;
    private Long parentUnitConstraintId;
   

    //======================================================


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentUnitConstraintId() {
        return parentUnitConstraintId;
    }

    public void setParentUnitConstraintId(Long parentUnitConstraintId) {
        this.parentUnitConstraintId = parentUnitConstraintId;
    }
}
