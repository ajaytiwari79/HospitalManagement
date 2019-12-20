package com.kairos.shiftplanning.domain.unit;

import com.kairos.shiftplanning.constraints.unitConstraint.UnitConstraints;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.List;

import static org.reflections.Reflections.log;

@XStreamAlias("Unit")
public class Unit {
    private String id;
    private UnitConstraints unitConstraints;



    public int checkActivityConstraints(List<ShiftImp> shifts, int index) {
        switch (index){
            case 1:return unitConstraints.getShiftOnWeekend().checkConstraints(shifts);


        }
        return 0;
    }

    public void breakActivityContraints( HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, int index) {
        log.debug("breaking Activity constraint: {}", index);
        switch (index) {
            case 1:
                unitConstraints.getShiftOnWeekend().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            default:
                break;
        }

    }

    public UnitConstraints getUnitConstraints() {
        return unitConstraints;
    }

    public void setUnitConstraints(UnitConstraints unitConstraints) {
        this.unitConstraints = unitConstraints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
