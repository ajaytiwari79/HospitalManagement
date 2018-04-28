package com.kairos.response.dto.web.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.response.dto.web.AgeRangeDTO;


import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 12/10/17.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTARuleTemplateDTO {
    private List<PhaseTemplateValueDTO> phaseTemplateValues;

    private RuleTemplateCategoryDTO ruleTemplateCategory;
    private WTATemplateType wtaTemplateType;
    private BigInteger id;
    private String name;
    private String templateType;
    private String category;
    private Long timeLimit;
    private List<String> balanceType;
    private Boolean checkAgainstTimeRules;
    private Long daysLimit;
    private Long minimumRest;//hh:mm
    private Long daysWorked;
    private Boolean disabled = false;
    private String description;
    private Long creationDate;
    private Long lastModificationDate;
    private Long nightsWorked;
    private Long intervalLength;
    private String intervalUnit;
    private Long validationStartDateMillis;
    private Boolean balanceAdjustment = false;
    private int recommendedValue;
    private Boolean useShiftTimes = false;
    private Long maximumAvgTime;
    private Double maximumVetoPercentage;
    private Long numberShiftsPerPeriod;
    private Long numberOfWeeks;
    private String fromDayOfWeek;
    private Long fromTime;
    private Boolean proportional = false;
    private Long toTime;
    private String toDayOfWeek;
    private Long continuousDayRestHours;
    private Long minimumDurationBetweenShifts;
    private Long continuousWeekRest;
    private Long averageRest;
    private String shiftAffiliation;
    private Long shiftsLimit;
    private Boolean onlyCompositeShifts;
    private String activityCode;
    private TimeBankTypeEnum frequency;
    private Integer yellowZone;
    private Boolean forbid;
    private Boolean allowExtraActivity;
    private List<AgeRangeDTO> ageRange;
    private List<Long> activities;

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public Boolean getForbid() {
        return forbid;
    }

    public Boolean getAllowExtraActivity() {
        return allowExtraActivity;
    }

    public int getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(int recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    public void setCheckAgainstTimeRules(Boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public Boolean getCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getBalanceAdjustment() {
        return balanceAdjustment;
    }


    public void setBalanceAdjustment(Boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public Boolean getUseShiftTimes() {
        return useShiftTimes;
    }

    public void setUseShiftTimes(Boolean useShiftTimes) {
        this.useShiftTimes = useShiftTimes;
    }

    public Boolean getProportional() {
        return proportional;
    }

    public void setProportional(Boolean proportional) {
        this.proportional = proportional;
    }

    public Boolean getOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(Boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RuleTemplateCategoryDTO getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategoryDTO ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
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

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public Long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(Long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public Long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(Long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public Long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(Long daysWorked) {
        this.daysWorked = daysWorked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(Long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public Long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(Long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public Long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(Long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }


    public Long getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(Long maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public Double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(Double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public Long getNumberShiftsPerPeriod() {
        return numberShiftsPerPeriod;
    }

    public void setNumberShiftsPerPeriod(Long numberShiftsPerPeriod) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
    }

    public Long getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(Long numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public String getFromDayOfWeek() {
        return fromDayOfWeek;
    }

    public void setFromDayOfWeek(String fromDayOfWeek) {
        this.fromDayOfWeek = fromDayOfWeek;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }


    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public String getToDayOfWeek() {
        return toDayOfWeek;
    }

    public void setToDayOfWeek(String toDayOfWeek) {
        this.toDayOfWeek = toDayOfWeek;
    }

    public Long getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(Long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public Long getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(Long minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public Long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(Long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public Long getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(Long averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public Long getShiftsLimit() {
        return shiftsLimit;
    }

    public void setShiftsLimit(Long shiftsLimit) {
        this.shiftsLimit = shiftsLimit;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
    public List<PhaseTemplateValueDTO> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValueDTO> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }

    public TimeBankTypeEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(TimeBankTypeEnum frequency) {
        this.frequency = frequency;
    }

    public Integer getYellowZone() {
        return yellowZone;
    }

    public void setYellowZone(Integer yellowZone) {
        this.yellowZone = yellowZone;
    }

    public Boolean isForbid() {
        return forbid;
    }

    public void setForbid(Boolean forbid) {
        this.forbid = forbid;
    }

    public Boolean isAllowExtraActivity() {
        return allowExtraActivity;
    }

    public void setAllowExtraActivity(Boolean allowExtraActivity) {
        this.allowExtraActivity = allowExtraActivity;
    }

    public List<AgeRangeDTO> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRangeDTO> ageRange) {
        this.ageRange = ageRange;
    }

    public List<Long> getActivities() {
        return activities;
    }

    public void setActivities(List<Long> activities) {
        this.activities = activities;
    }

    public WTARuleTemplateDTO() {
        //default const
    }

    public WTARuleTemplateDTO(String name, String templateType, boolean disabled, String description, long timeInMins, List<String> balanceTypes, boolean checkAgainstTimeRules) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.timeLimit = timeInMins;
        this.balanceType = balanceTypes;
        this.checkAgainstTimeRules = checkAgainstTimeRules;

    }

    // Template 13 Cons
    public WTARuleTemplateDTO(String name, String templateType, Boolean disabled, String description, Long numberShiftsPerPeriod, Long numberOfWeeks,
                              String fromDayOfWeek, Long fromTime, Boolean proportional,
                              String toDayOfWeek, Long toTime) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
        this.numberOfWeeks = numberOfWeeks;
        this.proportional = proportional;
        this.fromDayOfWeek = fromDayOfWeek;
        this.fromTime = fromTime;
        this.toDayOfWeek = toDayOfWeek;
        this.toTime = toTime;


    }

    public WTARuleTemplateDTO(String name, String templateType, Boolean disabled,
                              String description, Long intervalLength, Long validationStartDateMillis, String intervalUnit, Long daysLimit) {
        this.name = name;
        this.templateType = templateType;
        this.description = description;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.disabled = disabled;
        this.validationStartDateMillis = validationStartDateMillis;
        this.daysLimit = daysLimit;
    }

    // MinimumDailyRestingTimeWTATemplate
    public WTARuleTemplateDTO(String name, String templateType, Boolean disabled, String description, Long continuousDayRestHours) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.continuousDayRestHours = continuousDayRestHours;
    }

}
