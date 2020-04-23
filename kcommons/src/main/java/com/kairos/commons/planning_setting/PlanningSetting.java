package com.kairos.commons.planning_setting;

import com.kairos.enums.constraint.ConstraintLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanningSetting {

    private ConstraintLevel constraintLevel;
    private int constraintWeight;
    private boolean mandatory;

    public PlanningSetting(ConstraintLevel constraintLevel,int constraintWeight,boolean mandatory){
        this.constraintLevel =constraintLevel;
        this.constraintWeight = constraintWeight;
        this.mandatory  = mandatory;
    }


}
