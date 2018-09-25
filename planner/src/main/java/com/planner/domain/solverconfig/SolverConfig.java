package com.planner.domain.solverconfig;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

@Document
public class SolverConfig extends MongoBaseEntity {
    //Properties applicable common to both Country and Unit(Organization)
    private String name;//Unique(but not when copying)
    private String parentId;//copiedFromId;
    private String description;
    private Long phaseId;
    private Long planningPeriodId;
    private byte threadCount;
    private short terminationTimeInMinutes;
    private Long planningProblemId;
    private List<BigInteger> constraintIds;
    //Properties applicable only at Unit(Organization) level else null
    private Long unitId;
    //Properties applicable only at Country level else null
    private Long countryId;
    private Long parentCountryId;
    private Long organizationSubServiceId;

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

    public Long getOrganizationSubServiceId() {
        return organizationSubServiceId;
    }

    public void setOrganizationSubServiceId(Long organizationSubServiceId) {
        this.organizationSubServiceId = organizationSubServiceId;
    }

    public Long getParentCountryId() {
        return parentCountryId;
    }

    public void setParentCountryId(Long parentCountryId) {
        this.parentCountryId = parentCountryId;
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

    public SolverConfig setOrganizationSubServiceIdBuilder(Long organizationSubServiceId) {
        this.organizationSubServiceId = organizationSubServiceId;
        return this;
    }
    public SolverConfig setParentCountryIdBuilder(Long parentCountryId) {
        this.parentCountryId = parentCountryId;
        return this;
    }

}
