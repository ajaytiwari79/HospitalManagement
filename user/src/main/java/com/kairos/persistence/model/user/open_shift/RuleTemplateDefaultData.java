package com.kairos.persistence.model.user.open_shift;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.counter.CounterDTO;
import com.kairos.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.user.country.experties.ExpertiseResponseDTO;

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
    private List<CounterDTO> counters ;
    public RuleTemplateDefaultData() {
        //Default Constructor
    }

    public RuleTemplateDefaultData(List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities, List<OpenShiftIntervalDTO> intervals,
                                   List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises,Integer minOpenShiftHours,List<CounterDTO> counters) {
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.minOpenShiftHours=minOpenShiftHours;
        this.counters=counters;
    }

    public RuleTemplateDefaultData(List<OrganizationTypeAndSubType> organizationTypeAndSubType, List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities,
                                   List<OpenShiftIntervalDTO> intervals, List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises,List<CounterDTO> counters) {
        this.organizationTypeAndSubType = organizationTypeAndSubType;
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.counters=counters;
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

    public List<CounterDTO> getCounters() {
        return counters;
    }

    public void setCounters(List<CounterDTO> counters) {
        this.counters = counters;
    }
}
