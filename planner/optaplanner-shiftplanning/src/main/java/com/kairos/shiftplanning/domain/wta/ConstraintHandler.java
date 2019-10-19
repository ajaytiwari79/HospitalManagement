package com.kairos.shiftplanning.domain.wta;

import com.kairos.shiftplanning.constraints.ScoreLevel;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConstraintHandler {

     Logger log= LoggerFactory.getLogger(ConstraintHandler.class);

    ScoreLevel getLevel();
    int getWeight();
    default void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int contraintPenality){
        switch (getLevel()){
            case HARD:scoreHolder.addHardConstraintMatch(kContext,getWeight()*contraintPenality);
                log.debug("breaking constraint Hard: {}",getWeight()*contraintPenality);
                break;
            case MEDIUM:scoreHolder.addMediumConstraintMatch(kContext,getWeight()*contraintPenality);
                log.debug("breaking constraint Medium: {}",getWeight()*contraintPenality);
                break;
            case SOFT:scoreHolder.addSoftConstraintMatch(kContext,getWeight()*contraintPenality);
                log.debug("breaking constraint Soft: {}",getWeight()*contraintPenality);
                break;
            default:
                break;
        }
    }
}
