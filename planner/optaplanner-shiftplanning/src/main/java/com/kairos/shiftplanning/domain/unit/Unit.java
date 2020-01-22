package com.kairos.shiftplanning.domain.unit;

import com.kairos.shiftplanning.constraints.unitConstraint.UnitConstraints;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.List;

import static org.reflections.Reflections.log;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Unit")
public class Unit {
    private String id;
    private UnitConstraints unitConstraints;

    public int checkUnitConstraints(List<ShiftImp> shifts, int index) {
        switch (index){
            case 1:return unitConstraints.getShiftOnWeekend().checkConstraints(shifts);
            case 2:return unitConstraints.getPreferedEmployementType().checkConstraints(shifts);

        }
        return 0;
    }

    public void breakUnitContraints( HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, int index) {
        log.debug("breaking Unit constraint: {}", index);
        switch (index) {
            case 1:
                unitConstraints.getShiftOnWeekend().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            case 2:
                unitConstraints.getPreferedEmployementType().breakLevelConstraints(scoreHolder, kContext,constraintPenality);
                break;
            default:
                break;
        }

    }

}
