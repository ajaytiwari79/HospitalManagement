package com.kairos.persistence.model.user.open_shift;

import com.kairos.dto.ActivityDTO;
import com.kairos.dto.activity.TimeTypeDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.user.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.activity.open_shift.OpenShiftIntervalDTO;

import java.util.List;

public class RuleTemplateDefaultData {
    private List<OrganizationTypeAndSubType> organizationTypeAndSubType;
    private List<Skill> skills;
    private List<TimeTypeDTO> timeTypes;
    private List<ActivityDTO> activities;
    private List<OpenShiftIntervalDTO> intervals;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertises;
    private Integer minOpenShiftHours;

    public RuleTemplateDefaultData() {
        //Default Constructor
    }

    public RuleTemplateDefaultData(List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities, List<OpenShiftIntervalDTO> intervals, List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises,Integer minOpenShiftHours) {
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.minOpenShiftHours=minOpenShiftHours;
    }

    public RuleTemplateDefaultData(List<OrganizationTypeAndSubType> organizationTypeAndSubType, List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities, List<OpenShiftIntervalDTO> intervals, List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises) {
        this.organizationTypeAndSubType = organizationTypeAndSubType;
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
    }

    public List<OrganizationTypeAndSubType> getOrganizationTypeAndSubType() {
        return organizationTypeAndSubType;
    }

    public void setOrganizationTypeAndSubType(List<OrganizationTypeAndSubType> organizationTypeAndSubType) {
        this.organizationTypeAndSubType = organizationTypeAndSubType;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public List<OpenShiftIntervalDTO> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<OpenShiftIntervalDTO> intervals) {
        this.intervals = intervals;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<ExpertiseResponseDTO> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<ExpertiseResponseDTO> expertises) {
        this.expertises = expertises;
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }
}
