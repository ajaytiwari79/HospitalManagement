package com.kairos.dto.planner.constarints.unit;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class UnitConstraintDTO  {

    private Long unitId;
    private PlanningSetting planningSetting;
    private ConstraintSubType constraintSubType;

    public UnitConstraintDTO(Long unitId,PlanningSetting planningSetting, ConstraintSubType constraintSubType){
        this.unitId = unitId;
        this.planningSetting = planningSetting;
        this.constraintSubType = constraintSubType;
    }


}
