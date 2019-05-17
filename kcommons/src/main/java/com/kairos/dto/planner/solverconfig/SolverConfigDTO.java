package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.enums.TimeTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * @author mohit
 * @date - 21/9/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    }

    public SolverConfigDTO(List<ConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public List<ConstraintDTO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintDTO> constraints) {
        this.constraints = constraints;
    }


    //Setters and Getters

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }

    public Long getPlanningPeriodId() {
        return planningPeriodId;
    }

    public void setPlanningPeriodId(Long planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
    }

    public byte getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(byte threadCount) {
        this.threadCount = threadCount;
    }

    public short getTerminationTimeInMinutes() {
        return terminationTimeInMinutes;
    }

    public void setTerminationTimeInMinutes(short terminationTimeInMinutes) {
        this.terminationTimeInMinutes = terminationTimeInMinutes;
    }

    public Long getPlanningProblemId() {
        return planningProblemId;
    }

    public void setPlanningProblemId(Long planningProblemId) {
        this.planningProblemId = planningProblemId;
    }

    public List<BigInteger> getConstraintIds() {
        return constraintIds;
    }

    public void setConstraintIds(List<BigInteger> constraintIds) {
        this.constraintIds = constraintIds;
    }

    public TimeTypeEnum getTypeOfTimeType() {
        return typeOfTimeType;
    }

    public void setTypeOfTimeType(TimeTypeEnum typeOfTimeType) {
        this.typeOfTimeType = typeOfTimeType;
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

    public BigInteger getParentSolverConfigId() {
        return parentSolverConfigId;
    }

    public void setParentSolverConfigId(BigInteger parentSolverConfigId) {
        this.parentSolverConfigId = parentSolverConfigId;
    }
}
