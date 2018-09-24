package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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
    private BigInteger id;
    private String name;//Unique
    private String description;
    private Long phaseId;
    private Long planningPeriodId;
    private byte threadCount;
    private short terminationTimeInMinutes;
    private Long planningProblemId;
    private List<BigInteger> constraintIds;
    //Unit properties
    private Long unitId;
    //Country Properties
    private Long countryId;
    private Long parentCountryId;
    private Long organizationServiceCategoryId;

    //Constructors
    public SolverConfigDTO() {

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

    public Long getParentCountryId() {
        return parentCountryId;
    }

    public void setParentCountryId(Long parentCountryId) {
        this.parentCountryId = parentCountryId;
    }

    /*****************************SolverConfigDTO Builder****************************************/
    public SolverConfigDTO setNameBuilder(String name) {
        this.name = name;
        return this;
    }

    public SolverConfigDTO setDescriptionBuilder(String description) {
        this.description = description;
        return this;
    }

    public SolverConfigDTO setUnitIdBuilder(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public SolverConfigDTO setCountryIdBuilder(Long countryId) {
        this.countryId = countryId;
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

    public SolverConfigDTO setOrganizationServiceCategoryIdBuilder(Long organizationServiceCategoryId) {
        this.organizationServiceCategoryId = organizationServiceCategoryId;
        return this;
    }
    public SolverConfigDTO setParentCountryIdBuilder(Long parentCountryId) {
        this.parentCountryId = parentCountryId;
        return this;
    }

}
