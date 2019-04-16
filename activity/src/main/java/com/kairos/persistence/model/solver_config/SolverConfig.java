package com.kairos.persistence.model.solver_config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.solver_config.PlanningType;
import com.kairos.enums.solver_config.SolverConfigStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolverConfig extends MongoBaseEntity{

    private Long unitId;
    private String name;
    private boolean isTemplate;
    private PlanningType phase;
    private BigInteger parentSolverConfigId;
    private SolverConfigStatus status;
    private Integer terminationTime;//In minutes
    private List<ConstraintValue> constraints;
    private boolean isDefault;
    private int numberOfThread;
    private int plannerNumber;
    private Date lastSubmittedDate;


    public SolverConfig() {
    }

    public SolverConfig(Long unitId, String name, boolean isTemplate, PlanningType phase, BigInteger parentSolverConfigId, Integer terminationTime, List<ConstraintValue> constraints, boolean isDefault,SolverConfigStatus status) {
        this.unitId = unitId;
        this.name = name;
        this.isTemplate = isTemplate;
        this.phase = phase;
        this.parentSolverConfigId = parentSolverConfigId;
        this.terminationTime = terminationTime;
        this.constraints = constraints;
        this.isDefault = isDefault;
        this.status = status;
    }

    public Date getLastSubmittedDate() {
        return lastSubmittedDate;
    }

    public void setLastSubmittedDate(Date lastSubmittedDate) {
        this.lastSubmittedDate = lastSubmittedDate;
    }

    public int getNumberOfThread() {
        return numberOfThread;
    }

    public void setNumberOfThread(int numberOfThread) {
        this.numberOfThread = numberOfThread;
    }

    public int getPlannerNumber() {
        return plannerNumber;
    }

    public void setPlannerNumber(int plannerNumber) {
        this.plannerNumber = plannerNumber;
    }

    public SolverConfigStatus getStatus() {
        return status;
    }

    public void setStatus(SolverConfigStatus status) {
        this.status = status;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public List<ConstraintValue> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintValue> constraints) {
        this.constraints = constraints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public PlanningType getPhase() {
        return phase;
    }

    public void setPhase(PlanningType phase) {
        this.phase = phase;
    }


    public BigInteger getParentSolverConfigId() {
        return parentSolverConfigId;
    }

    public void setParentSolverConfigId(BigInteger parentSolverConfigId) {
        this.parentSolverConfigId = parentSolverConfigId;
    }

    public Integer getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(Integer terminationTime) {
        this.terminationTime = terminationTime;
    }
}
