package com.planner.domain.solverconfig;

import com.planner.domain.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class SolverConfig extends MongoBaseEntity {

    private String name;//Unique
    private String description;
    private Long unitId;
    private Long countryId;
    private Long phaseId;
    private Long planningPeriodId;
    private byte threadCount;
    private short terminationTimeInMinutes;
    private Long planningProblemId;
    private List<BigInteger> constraintIds;
    private Long organizationServiceCategoryId;

    //Constructors
    public SolverConfig() {

    }

    //Setters using Builder Patten and Getters
    public String getName() {
        return name;
    }

    public SolverConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SolverConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public SolverConfig setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public Long getCountryId() {
        return countryId;
    }

    public SolverConfig setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public SolverConfig setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
        return this;
    }

    public Long getPlanningPeriodId() {
        return planningPeriodId;
    }

    public SolverConfig setPlanningPeriodId(Long planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
        return this;
    }

    public byte getThreadCount() {
        return threadCount;
    }

    public SolverConfig setThreadCount(byte threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public short getTerminationTimeInMinutes() {
        return terminationTimeInMinutes;
    }

    public SolverConfig setTerminationTimeInMinutes(short terminationTimeInMinutes) {
        this.terminationTimeInMinutes = terminationTimeInMinutes;
        return this;
    }

    public Long getPlanningProblemId() {
        return planningProblemId;
    }

    public SolverConfig setPlanningProblemId(Long planningProblemId) {
        this.planningProblemId = planningProblemId;
        return this;
    }

    public List<BigInteger> getConstraintIds() {
        return constraintIds;
    }

    public SolverConfig setConstraintIds(List<BigInteger> constraintIds) {
        this.constraintIds = constraintIds;
        return this;
    }

    public Long getOrganizationServiceCategoryId() {
        return organizationServiceCategoryId;
    }

    public SolverConfig setOrganizationServiceCategoryId(Long organizationServiceCategoryId) {
        this.organizationServiceCategoryId = organizationServiceCategoryId;
        return this;
    }
}
