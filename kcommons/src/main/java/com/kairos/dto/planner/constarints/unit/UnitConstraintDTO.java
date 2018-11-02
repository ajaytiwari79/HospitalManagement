package com.kairos.dto.planner.constarints.unit;

import com.kairos.dto.planner.constarints.ConstraintDTO;

public class UnitConstraintDTO extends ConstraintDTO {
    //~
    private Long unitId;
    private Long parentCountryConstraintId;
   

    //======================================================


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentCountryConstraintId() {
        return parentCountryConstraintId;
    }

    public void setParentCountryConstraintId(Long parentCountryConstraintId) {
        this.parentCountryConstraintId = parentCountryConstraintId;
    }
}
