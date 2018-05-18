package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.enums.PriorityGroup.ShiftSelectionType;

import java.math.BigInteger;
import java.util.List;

public class OpenShiftRuleTemplateDTO {
    private BigInteger id;
    private String name;
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private List<ActivitiesPerTimeType> selectedSets;
    private List<Long> selectedSkills;
    private boolean underStaffingBeforeStart;
    private String underStaffingBeforeStartDays;
    private boolean underStaffingPromptPlanner;
    private boolean overStaffingBeforeStart;
    private String overStaffingBeforeStartDays;
    private boolean overStaffingPromptPlanner;
    private boolean skillMissingBeforeStart;
    private String skillMissingBeforeStartDays;
    private boolean skillMissingPromptPlanner;
    private ShiftSelectionType shiftSelectionType;


    public OpenShiftRuleTemplateDTO() {
        //Default Constructor
    }

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

    public Long getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(Long organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public Long getOrganizationSubTypeId() {
        return organizationSubTypeId;
    }

    public void setOrganizationSubTypeId(Long organizationSubTypeId) {
        this.organizationSubTypeId = organizationSubTypeId;
    }

    public List<ActivitiesPerTimeType> getSelectedSets() {
        return selectedSets;
    }

    public void setSelectedSets(List<ActivitiesPerTimeType> selectedSets) {
        this.selectedSets = selectedSets;
    }

    public List<Long> getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(List<Long> selectedSkills) {
        this.selectedSkills = selectedSkills;
    }

    public boolean isUnderStaffingBeforeStart() {
        return underStaffingBeforeStart;
    }

    public void setUnderStaffingBeforeStart(boolean underStaffingBeforeStart) {
        this.underStaffingBeforeStart = underStaffingBeforeStart;
    }

    public String getUnderStaffingBeforeStartDays() {
        return underStaffingBeforeStartDays;
    }

    public void setUnderStaffingBeforeStartDays(String underStaffingBeforeStartDays) {
        this.underStaffingBeforeStartDays = underStaffingBeforeStartDays;
    }

    public boolean isUnderStaffingPromptPlanner() {
        return underStaffingPromptPlanner;
    }

    public void setUnderStaffingPromptPlanner(boolean underStaffingPromptPlanner) {
        this.underStaffingPromptPlanner = underStaffingPromptPlanner;
    }

    public boolean isOverStaffingBeforeStart() {
        return overStaffingBeforeStart;
    }

    public void setOverStaffingBeforeStart(boolean overStaffingBeforeStart) {
        this.overStaffingBeforeStart = overStaffingBeforeStart;
    }

    public String getOverStaffingBeforeStartDays() {
        return overStaffingBeforeStartDays;
    }

    public void setOverStaffingBeforeStartDays(String overStaffingBeforeStartDays) {
        this.overStaffingBeforeStartDays = overStaffingBeforeStartDays;
    }

    public boolean isOverStaffingPromptPlanner() {
        return overStaffingPromptPlanner;
    }

    public void setOverStaffingPromptPlanner(boolean overStaffingPromptPlanner) {
        this.overStaffingPromptPlanner = overStaffingPromptPlanner;
    }

    public boolean isSkillMissingBeforeStart() {
        return skillMissingBeforeStart;
    }

    public void setSkillMissingBeforeStart(boolean skillMissingBeforeStart) {
        this.skillMissingBeforeStart = skillMissingBeforeStart;
    }

    public String getSkillMissingBeforeStartDays() {
        return skillMissingBeforeStartDays;
    }

    public void setSkillMissingBeforeStartDays(String skillMissingBeforeStartDays) {
        this.skillMissingBeforeStartDays = skillMissingBeforeStartDays;
    }

    public boolean isSkillMissingPromptPlanner() {
        return skillMissingPromptPlanner;
    }

    public void setSkillMissingPromptPlanner(boolean skillMissingPromptPlanner) {
        this.skillMissingPromptPlanner = skillMissingPromptPlanner;
    }



    public ShiftSelectionType getShiftSelectionType() {
        return shiftSelectionType;
    }

    public void setShiftSelectionType(ShiftSelectionType shiftSelectionType) {
        this.shiftSelectionType = shiftSelectionType;
    }


}
