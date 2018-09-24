package com.planner.domain.solverconfig;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

@Document
public class SolverConfig extends MongoBaseEntity {

    private String name;//Unique
    private String parentId;//copiedFromId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public Long getOrganizationServiceCategoryId() {
        return organizationServiceCategoryId;
    }

    public void setOrganizationServiceCategoryId(Long organizationServiceCategoryId) {
        this.organizationServiceCategoryId = organizationServiceCategoryId;
    }

    /*****************************SolverConfig Builder****************************************/
    public SolverConfig setIdBuilder(String id) {
        this.id = id;
        return this;
    }

    public SolverConfig setNameBuilder(String name) {
        this.name = name;
        return this;
    }
    public SolverConfig setParentIdBuilder(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public SolverConfig setDescriptionBuilder(String description) {
        this.description = description;
        return this;
    }

    public SolverConfig setUnitIdBuilder(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public SolverConfig setCountryIdBuilder(Long countryId) {
        this.countryId = countryId;
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

    public SolverConfig setOrganizationServiceCategoryIdBuilder(Long organizationServiceCategoryId) {
        this.organizationServiceCategoryId = organizationServiceCategoryId;
        return this;
    }
}
