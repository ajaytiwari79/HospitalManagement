package com.kairos.commons.planning_setting;

import com.kairos.enums.constraint.ConstraintLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@NoArgsConstructor
public class PlanningSetting {
    Logger log= LoggerFactory.getLogger(PlanningSetting .class);
    private ConstraintLevel constraintLevel;
    private int constraintWeight;

    public PlanningSetting(ConstraintLevel constraintLevel,int constraintWeight){
        this.constraintLevel =constraintLevel;
        this.constraintWeight = constraintWeight;
    }


}
