package com.kairos.persistence.model.open_shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.open_shift.Priority;
import com.kairos.enums.AllowedLength;
import com.kairos.enums.OpenShiftRuleTemplateType;
import com.kairos.dto.activity.open_shift.ActivitiesPerTimeType;
import com.kairos.dto.activity.open_shift.PlannerNotificationInfo;
import com.kairos.dto.activity.open_shift.ShiftAssignmentCriteria;

import java.math.BigInteger;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenShiftRuleTemplateDTO {
    private BigInteger id;
    private String name;
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private OpenShiftRuleTemplateType ruleTemplateType;
    private List<ActivitiesPerTimeType> activitiesPerTimeTypes;
    private List<Long> selectedSkills;
    private boolean promptPlanner;
    private Long unitId;
    private Long countryId;
    private BigInteger parentId;
    private FeatureRules featureRules;
    private NotificationWay notificationWay;
    private AllowedLength allowedLength;
    private Integer minimumShiftHours;
    private Integer maximumShiftHours;
    private ShiftAssignmentCriteria shiftAssignmentCriteria;
    private BigInteger openShiftIntervalId;
    private PlannerNotificationInfo plannerNotificationInfo;
    private Priority priority;
    private OpenShiftInterval openShiftInterval;


    public OpenShiftRuleTemplateDTO() {
        //Default Constructor
    }

    public OpenShiftInterval getOpenShiftInterval() {
        return openShiftInterval;
    }

    public void setOpenShiftInterval(OpenShiftInterval openShiftInterval) {
        this.openShiftInterval = openShiftInterval;
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

    public boolean isPromptPlanner() {
        return promptPlanner;
    }

    public void setPromptPlanner(boolean promptPlanner) {
        this.promptPlanner = promptPlanner;
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

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public FeatureRules getFeatureRules() {
        return featureRules;
    }

    public void setFeatureRules(FeatureRules featureRules) {
        this.featureRules = featureRules;
    }

    public NotificationWay getNotificationWay() {
        return notificationWay;
    }

    public void setNotificationWay(NotificationWay notificationWay) {
        this.notificationWay = notificationWay;
    }

    public AllowedLength getAllowedLength() {
        return allowedLength;
    }

    public void setAllowedLength(AllowedLength allowedLength) {
        this.allowedLength = allowedLength;
    }

    public Integer getMinimumShiftHours() {
        return minimumShiftHours;
    }

    public void setMinimumShiftHours(Integer minimumShiftHours) {
        this.minimumShiftHours = minimumShiftHours;
    }

    public Integer getMaximumShiftHours() {
        return maximumShiftHours;
    }

    public void setMaximumShiftHours(Integer maximumShiftHours) {
        this.maximumShiftHours = maximumShiftHours;
    }

    public ShiftAssignmentCriteria getShiftAssignmentCriteria() {
        return shiftAssignmentCriteria;
    }

    public void setShiftAssignmentCriteria(ShiftAssignmentCriteria shiftAssignmentCriteria) {
        this.shiftAssignmentCriteria = shiftAssignmentCriteria;
    }

    public BigInteger getOpenShiftIntervalId() {
        return openShiftIntervalId;
    }

    public void setOpenShiftIntervalId(BigInteger openShiftIntervalId) {
        this.openShiftIntervalId = openShiftIntervalId;
    }

    public PlannerNotificationInfo getPlannerNotificationInfo() {
        return plannerNotificationInfo;
    }

    public void setPlannerNotificationInfo(PlannerNotificationInfo plannerNotificationInfo) {
        this.plannerNotificationInfo = plannerNotificationInfo;
    }

    public OpenShiftRuleTemplateType getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(OpenShiftRuleTemplateType ruleTemplateType) {
        this.ruleTemplateType = ruleTemplateType;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
