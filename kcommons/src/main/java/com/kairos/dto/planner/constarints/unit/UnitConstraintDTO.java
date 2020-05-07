package com.kairos.dto.planner.constarints.unit;

import com.kairos.commons.planning_setting.ConstraintSetting;
import com.kairos.enums.constraint.ConstraintSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnitConstraintDTO  {

    private Long unitId;
    private ConstraintSetting constraintSetting;
    private ConstraintSubType constraintSubType;
    private Boolean mandatory;

    public UnitConstraintDTO(ConstraintSetting constraintSetting, ConstraintSubType constraintSubType){
        this.constraintSetting = constraintSetting;
        this.constraintSubType = constraintSubType;
    }


}
