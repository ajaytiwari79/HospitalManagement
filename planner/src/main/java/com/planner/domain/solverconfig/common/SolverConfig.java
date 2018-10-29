package com.planner.domain.solverconfig.common;

import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

@Document(collection = "solverConfig")
public class SolverConfig extends MongoBaseEntity {

    protected String name;//Unique(but not when copying)
    protected String description;
    protected Long phaseId;
    protected Long planningPeriodId;
    protected byte threadCount;
    protected short terminationTimeInMinutes;
    protected Long planningProblemId;
    protected List<BigInteger> constraintIds;
    protected BigInteger parentCountrySolverConfigId;//copiedFrom


    //Constructors
    public SolverConfig() {

    }
   /***********************************************/
    //Setters and Getters
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




    /*****************************SolverConfig Builder****************************************/
    public SolverConfig setIdBuilder(BigInteger id) {
        this.id = id;
        return this;
    }

    public SolverConfig setNameBuilder(String name) {
        this.name = name;
        return this;
    }



    public SolverConfig setDescriptionBuilder(String description) {
        this.description = description;
        return this;
    }


    public SolverConfig setPhaseIdBuilder(Long phaseId) {
        this.phaseId = phaseId;
        return this;
    }

    public SolverConfig setPlanningPeriodIdBuilder(Long planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
        return this;
    }

    public SolverConfig setThreadCountBuilder(byte threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public SolverConfig setTerminationTimeInMinutesBuilder(short terminationTimeInMinutes) {
        this.terminationTimeInMinutes = terminationTimeInMinutes;
        return this;
    }

    public SolverConfig setPlanningProblemIdBuilder(Long planningProblemId) {
        this.planningProblemId = planningProblemId;
        return this;
    }

    public SolverConfig setConstraintIdsBuilder(List<BigInteger> constraintIds) {
        this.constraintIds = constraintIds;
        return this;
    }

    public BigInteger getParentCountrySolverConfigId() {
        return parentCountrySolverConfigId;
    }

    public void setParentCountrySolverConfigId(BigInteger parentCountrySolverConfigId) {
        this.parentCountrySolverConfigId = parentCountrySolverConfigId;
    }
}
