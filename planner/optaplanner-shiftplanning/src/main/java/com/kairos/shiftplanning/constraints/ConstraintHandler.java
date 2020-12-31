package com.kairos.shiftplanning.constraints;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface ConstraintHandler {
    Logger log= LoggerFactory.getLogger(ConstraintHandler.class);

    default int checkConstraints(Activity activity, ShiftImp shift){ return 0;}
    default int checkConstraints(List<ShiftImp> shifts){
        return 0;
    }

    default int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps){return 0;}


    default int verifyConstraints(Activity activity, Shift shift){ return 0;}
    default int verifyConstraints(List<Shift> shifts){
        return 0;
    }

    int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps);

    ScoreLevel getLevel();

    //aa
    int getWeight();

    //bb
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
