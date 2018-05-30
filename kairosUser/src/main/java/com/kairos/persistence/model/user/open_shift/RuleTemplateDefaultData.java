package com.kairos.persistence.model.user.open_shift;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;

import java.util.List;

public class RuleTemplateDefaultData {
    private List<OrganizationTypeAndSubType> organizationTypeAndSubType;
    private List<Skill> skills;
    private List<TimeTypeDTO> timeTypes;
    private List<ActivityDTO> activities;
    private List<OpenShiftIntervalDTO> intervals;

    public RuleTemplateDefaultData() {
        //Default Constructor
    }

    public RuleTemplateDefaultData(List<OrganizationTypeAndSubType> organizationTypeAndSubType, List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities, List<OpenShiftIntervalDTO> intervals) {
        this.organizationTypeAndSubType = organizationTypeAndSubType;
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
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
}
