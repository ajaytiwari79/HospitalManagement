package com.kairos.shiftplanning.domain.unit;

import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Unit")
public class Unit {
    private String id;
    Map<ConstraintSubType, Constraint> constraints;

    public int checkConstraints(List<ShiftImp> shifts, ConstraintType constraintType) {
        return constraints.get(constraintType).checkConstraints(shifts);
    }

    public void breakContraints( HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, ConstraintType constraintType) {
        constraints.get(constraintType).breakLevelConstraints(scoreHolder,kContext,constraintPenality);
    }

}
