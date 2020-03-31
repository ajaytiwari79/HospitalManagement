package com.kairos.shiftplanning.domain.unit;

import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
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
    private Long id;
    private Map<ConstraintSubType, Constraint> constraints;
    private Map<Long, DayTypeDTO> dayTypeMap;
    private UserAccessRoleDTO user;
    private Phase phase;
    private PlanningPeriod planningPeriod;
    private Map<String, TimeSlotWrapper> timeSlotWrapperMap;

    public int checkConstraints(List<ShiftImp> shifts, ConstraintType constraintType) {
        return constraints.get(constraintType).checkConstraints(shifts);
    }

    public void breakContraints( HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, ConstraintType constraintType) {
        constraints.get(constraintType).breakLevelConstraints(scoreHolder,kContext,constraintPenality);
    }

}
