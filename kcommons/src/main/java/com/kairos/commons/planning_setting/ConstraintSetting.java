package com.kairos.commons.planning_setting;

import com.kairos.enums.constraint.ScoreLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@Getter
@Setter
@NoArgsConstructor
public class ConstraintSetting {

    private ScoreLevel scoreLevel;
    private int constraintWeight;
    private boolean mandatory;

    public ScoreLevel getScoreLevel() {
        return isNullOrElse(scoreLevel,ScoreLevel.SOFT);
    }

    public ConstraintSetting(ScoreLevel scoreLevel, int constraintWeight, boolean mandatory){
        this.scoreLevel = scoreLevel;
        this.constraintWeight = constraintWeight;
        this.mandatory  = mandatory;
    }


}
