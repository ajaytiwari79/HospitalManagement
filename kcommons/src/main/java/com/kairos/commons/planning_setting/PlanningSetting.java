package com.kairos.commons.planning_setting;

import com.kairos.enums.constraint.ScoreLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanningSetting {

    private ScoreLevel scoreLevel;
    private int constraintWeight;
    private boolean mandatory;

    public PlanningSetting(ScoreLevel scoreLevel, int constraintWeight, boolean mandatory){
        this.scoreLevel = scoreLevel;
        this.constraintWeight = constraintWeight;
        this.mandatory  = mandatory;
    }


}
