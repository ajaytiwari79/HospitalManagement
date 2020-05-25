package com.kairos.dto.planner.solverconfig;

import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class DefaultDataDTO {

    private List<OrganizationServiceDTO> organizationServices;
    private List<PhaseDTO> phases;
    private List<PlanningPeriodDTO> planningPeriods;
    private List<TimeTypeEnum> timeTypeEnums;
    private Map<ConstraintType, Set<ConstraintDTO>> constraintTypes;
    private List<PlanningProblemDTO> planningProblems;

}
