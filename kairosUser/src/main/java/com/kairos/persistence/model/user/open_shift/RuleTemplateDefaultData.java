package com.kairos.persistence.model.user.open_shift;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.model.user.skill.Skill;

import java.util.List;

public class RuleTemplateDefaultData {
    private List<OrganizationTypeAndSubType> organizationTypeAndSubType;
    private List<Skill> skills;
    private List<TimeType> timeTypes;
    private List<ActivityDTO> activities;

    public RuleTemplateDefaultData() {
        //Default Constructor
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

    public List<TimeType> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeType> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
