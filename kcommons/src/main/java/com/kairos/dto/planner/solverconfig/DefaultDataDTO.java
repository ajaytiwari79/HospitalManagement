package com.kairos.dto.planner.solverconfig;

import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultDataDTO {

    private List<OrganizationServiceDTO> organizationServices;
    private List<PhaseDTO> phases;
    private List<PlanningPeriodDTO> planningPeriods;
    private List<TimeTypeEnum> timeTypeEnums;
    private Map<ConstraintType, Set<ConstraintSubType>> constraintTypes;
    private List<PlanningProblemDTO> planningProblems;

    public Map<ConstraintType, Set<ConstraintSubType>> getConstraintTypes() {
        return constraintTypes;
    }

    public void setConstraintTypes(Map<ConstraintType, Set<ConstraintSubType>> constraintTypes) {
        this.constraintTypes = constraintTypes;
    }

    public List<OrganizationServiceDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationServiceDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<PhaseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDTO> phases) {
        this.phases = phases;
    }

    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return planningPeriods;
    }

    public void setPlanningPeriods(List<PlanningPeriodDTO> planningPeriods) {
        this.planningPeriods = planningPeriods;
    }

    public List<TimeTypeEnum> getTimeTypeEnums() {
        return timeTypeEnums;
    }

    public void setTimeTypeEnums(List<TimeTypeEnum> timeTypeEnums) {
        this.timeTypeEnums = timeTypeEnums;
    }

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
