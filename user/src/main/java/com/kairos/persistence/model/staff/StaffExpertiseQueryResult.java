package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;
import java.util.List;

/**
 * Created by pavan on 7/5/18.
 */

@QueryResult
public class StaffExpertiseQueryResult {
    private Long id;
    private String name;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;
    @DateLong
    private Date expertiseStartDate;
    private Integer nextSeniorityLevelInMonths;
    private List<SeniorityLevel> seniorityLevels;
    private Sector sector;
    private SeniorityLevel seniorityLevel;
    private boolean unitPositionExists;

    public StaffExpertiseQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Integer getRelevantExperienceInMonths() {
        return relevantExperienceInMonths;
    }

    public void setRelevantExperienceInMonths(Integer relevantExperienceInMonths) {
        this.relevantExperienceInMonths = relevantExperienceInMonths;
    }

    public Date getExpertiseStartDate() {
        return expertiseStartDate;
    }

    public void setExpertiseStartDate(Date expertiseStartDate) {
        this.expertiseStartDate = expertiseStartDate;
    }

    public Integer getNextSeniorityLevelInMonths() {
        return nextSeniorityLevelInMonths;
    }

    public void setNextSeniorityLevelInMonths(Integer nextSeniorityLevelInMonths) {
        this.nextSeniorityLevelInMonths = nextSeniorityLevelInMonths;
    }

    public List<SeniorityLevel> getSeniorityLevels() {
        return seniorityLevels;
    }

    public void setSeniorityLevels(List<SeniorityLevel> seniorityLevels) {
        this.seniorityLevels = seniorityLevels;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public boolean isUnitPositionExists() {
        return unitPositionExists;
    }

    public void setUnitPositionExists(boolean unitPositionExists) {
        this.unitPositionExists = unitPositionExists;
    }
}
