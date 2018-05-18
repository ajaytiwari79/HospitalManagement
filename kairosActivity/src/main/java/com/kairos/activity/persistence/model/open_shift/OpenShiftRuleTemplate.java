package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.enums.PriorityGroup.ShiftSelectionType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class OpenShiftRuleTemplate extends MongoBaseEntity {

    private String name;
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private List<ActivitiesPerTimeType> activitiesPerTimeTypes;
    private List<Long> selectedSkills;
    private boolean underStaffingBeforeStart;
    private Integer underStaffingBeforeStartDays;
    private boolean underStaffingPromptPlanner;
    private boolean overStaffingBeforeStart;
    private Integer overStaffingBeforeStartDays;
    private boolean overStaffingPromptPlanner;
    private boolean skillMissingBeforeStart;
    private Integer skillMissingBeforeStartDays;
    private boolean skillMissingPromptPlanner;
    private Long unitId;
    private Long countryId;
    private BigInteger countryParentId;
    private ShiftSelectionType shiftSelectionType;


    public OpenShiftRuleTemplate() {
        //Default Constructor
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

    public List<ActivitiesPerTimeType> getActivitiesPerTimeTypes() {
        return activitiesPerTimeTypes;
    }

    public void setActivitiesPerTimeTypes(List<ActivitiesPerTimeType> activitiesPerTimeTypes) {
        this.activitiesPerTimeTypes = activitiesPerTimeTypes;
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

    public Integer getUnderStaffingBeforeStartDays() {
        return underStaffingBeforeStartDays;
    }

    public void setUnderStaffingBeforeStartDays(Integer underStaffingBeforeStartDays) {
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

    public Integer getOverStaffingBeforeStartDays() {
        return overStaffingBeforeStartDays;
    }

    public void setOverStaffingBeforeStartDays(Integer overStaffingBeforeStartDays) {
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

    public Integer getSkillMissingBeforeStartDays() {
        return skillMissingBeforeStartDays;
    }

    public void setSkillMissingBeforeStartDays(Integer skillMissingBeforeStartDays) {
        this.skillMissingBeforeStartDays = skillMissingBeforeStartDays;
    }

    public boolean isSkillMissingPromptPlanner() {
        return skillMissingPromptPlanner;
    }

    public void setSkillMissingPromptPlanner(boolean skillMissingPromptPlanner) {
        this.skillMissingPromptPlanner = skillMissingPromptPlanner;
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

    public BigInteger getCountryParentId() {
        return countryParentId;
    }

    public void setCountryParentId(BigInteger countryParentId) {
        this.countryParentId = countryParentId;
    }

    public ShiftSelectionType getShiftSelectionType() {
        return shiftSelectionType;
    }

    public void setShiftSelectionType(ShiftSelectionType shiftSelectionType) {
        this.shiftSelectionType = shiftSelectionType;
    }

}
