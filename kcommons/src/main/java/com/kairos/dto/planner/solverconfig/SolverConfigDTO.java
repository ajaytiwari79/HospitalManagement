package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author mohit
 * @date - 21/9/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SolverConfigDTO {
    //Common
    protected BigInteger id;
    @NotBlank(message = "error.name.notnull")
    protected String name;//Unique
    protected String description;
    @NotNull(message = "error.phaseId.not.exists")
    protected Long phaseId;
    protected Long planningPeriodId;
    protected byte threadCount;
    protected short terminationTimeInMinutes;
    @NotNull(message = "error.planningproblem.not.exists")
    protected Long planningProblemId;
    protected List<BigInteger> constraintIds;
    protected BigInteger parentSolverConfigId;
    private List<ConstraintDTO> constraints;
    @NotNull(message = "error.timetype.not.exists")
    protected TimeTypeEnum typeOfTimeType;

    public SolverConfigDTO() {
        this.constraints = new ArrayList<>();
    }

    public SolverConfigDTO(List<ConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public void setConstraints(List<ConstraintDTO> constraints) {
        this.constraints = isNullOrElse(constraints,new ArrayList<>());
    }


    public SolverConfigDTO setNameBuilder(String name) {
        this.name = name;
        return this;
    }

    public SolverConfigDTO setDescriptionBuilder(String description) {
        this.description = description;
        return this;
    }


    public SolverConfigDTO setPhaseIdBuilder(Long phaseId) {
        this.phaseId = phaseId;
        return this;
    }

    public SolverConfigDTO setPlanningPeriodIdBuilder(Long planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
        return this;
    }

    public SolverConfigDTO setThreadCountBuilder(byte threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public SolverConfigDTO setTerminationTimeInMinutesBuilder(short terminationTimeInMinutes) {
        this.terminationTimeInMinutes = terminationTimeInMinutes;
        return this;
    }

    public SolverConfigDTO setPlanningProblemIdBuilder(Long planningProblemId) {
        this.planningProblemId = planningProblemId;
        return this;
    }

    public SolverConfigDTO setConstraintIdsBuilder(List<BigInteger> constraintIds) {
        this.constraintIds = constraintIds;
        return this;
    }

}
