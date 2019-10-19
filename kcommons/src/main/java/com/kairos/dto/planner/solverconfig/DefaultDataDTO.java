package com.kairos.dto.planner.solverconfig;

import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
@Getter
@Setter
public class DefaultDataDTO {

    private List<OrganizationServiceDTO> organizationServices;
    private List<PhaseDTO> phases;
    private List<PlanningPeriodDTO> planningPeriods;
    private List<TimeTypeEnum> timeTypeEnums;
    private Map<ConstraintType, Set<ConstraintSubType>> constraintTypes;
    private List<PlanningProblemDTO> planningProblems;

    public DefaultDataDTO setPlanningProblemsBuilder(List<PlanningProblemDTO> planningProblems) {
        this.planningProblems = planningProblems;
        return this;
    }

    public DefaultDataDTO setConstraintTypesBuilder(Map<ConstraintType, Set<ConstraintSubType>> constraintTypes) {
        this.constraintTypes = constraintTypes;
        return this;
    }

    public DefaultDataDTO setOrganizationServicesBuilder(List<OrganizationServiceDTO> organizationServiceDTOS) {
        this.organizationServices = organizationServiceDTOS;
        return this;
    }
    public DefaultDataDTO setPhaseDTOSBuilder(List<PhaseDTO> phaseDTOS) {
        this.phases = phaseDTOS;
        return this;
    }

    public DefaultDataDTO setPlanningPeriodBuilder(List<PlanningPeriodDTO> planningPeriodDTOS) {
        this.planningPeriods = planningPeriodDTOS;
        return this;
    }

    public DefaultDataDTO setTimeTypeEnumSBuilder(List<TimeTypeEnum> timeTypeEnums) {
        this.timeTypeEnums = timeTypeEnums;
        return this;
    }
}
