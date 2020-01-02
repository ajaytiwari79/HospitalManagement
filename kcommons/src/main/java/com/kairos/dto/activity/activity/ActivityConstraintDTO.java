package com.kairos.dto.activity.activity;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.commons.utils.NotNullOrEmpty;
import com.kairos.enums.constraint.ConstraintSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class ActivityConstraintDTO {

    private BigInteger activityId;
    private PlanningSetting planningSetting;
    private ConstraintSubType constraintSubType;
    private Boolean mandatory;

    public ActivityConstraintDTO( PlanningSetting planningSetting,ConstraintSubType constraintSubType){
        this.planningSetting = planningSetting;
        this.constraintSubType = constraintSubType;
    }
}
