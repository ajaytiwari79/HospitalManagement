package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.TimeCalculationActivityDTO;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.activity.tabs.SkillActivityTab;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 8/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityWithCompositeDTO {

    private BigInteger id;
    private String name;
    private GeneralActivityTab generalActivityTab;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private List<CompositeActivityDTO> compositeActivities= new ArrayList<>();
    private List<Long> expertises= new ArrayList<>();
    private List<Long> employmentTypes= new ArrayList<>();
    private RulesActivityTab rulesActivityTab;
    private SkillActivityTab skillActivityTab;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;
    private boolean allowChildActivities;
    private Long staffId;
    private BigInteger activityId;
    private Long unitPositionId;
    private Long unitId;
    private Short shortestTime;
    private Short longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;
    private List<Long> dayTypeIds= new ArrayList<>();


    public ActivityWithCompositeDTO() {
        //Default Constructor
    }

    public TimeCalculationActivityDTO getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityDTO timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
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


    public List<CompositeActivityDTO> getCompositeActivities() {
        return compositeActivities;
    }

    public void setCompositeActivities(List<CompositeActivityDTO> compositeActivities) {
        this.compositeActivities = compositeActivities;
    }


    public GeneralActivityTab getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTab generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public RulesActivityTab getRulesActivityTab() {
        return rulesActivityTab;
    }

    public void setRulesActivityTab(RulesActivityTab rulesActivityTab) {
        this.rulesActivityTab = rulesActivityTab;
    }

    public SkillActivityTab getSkillActivityTab() {
        return skillActivityTab;
    }

    public void setSkillActivityTab(SkillActivityTab skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }

    public PhaseSettingsActivityTab getPhaseSettingsActivityTab() {
        return phaseSettingsActivityTab;
    }

    public void setPhaseSettingsActivityTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }

    public BalanceSettingsActivityTab getBalanceSettingsActivityTab() {
        return balanceSettingsActivityTab;
    }

    public void setBalanceSettingsActivityTab(BalanceSettingsActivityTab balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
    }

    public boolean isAllowChildActivities() {
        return allowChildActivities;
    }

    public void setAllowChildActivities(boolean allowChildActivities) {
        this.allowChildActivities = allowChildActivities;
    }

    public LocalTime getEarliestStartTime() {
        return earliestStartTime;
    }

    public void setEarliestStartTime(LocalTime earliestStartTime) {
        this.earliestStartTime = earliestStartTime;
    }

    public LocalTime getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(LocalTime latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public Short getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(Short shortestTime) {
        this.shortestTime = shortestTime;
    }

    public Short getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(Short longestTime) {
        this.longestTime = longestTime;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxThisActivityPerShift() {
        return maxThisActivityPerShift;
    }

    public void setMaxThisActivityPerShift(Integer maxThisActivityPerShift) {
        this.maxThisActivityPerShift = maxThisActivityPerShift;
    }

    public boolean isEligibleForMove() {
        return eligibleForMove;
    }

    public void setEligibleForMove(boolean eligibleForMove) {
        this.eligibleForMove = eligibleForMove;
    }

    public LocalTime getMaximumEndTime() {
        return maximumEndTime;
    }

    public void setMaximumEndTime(LocalTime maximumEndTime) {
        this.maximumEndTime = maximumEndTime;
    }

    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
    }
}
