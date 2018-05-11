package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.persistence.model.user.agreement.wta.templates.BreakTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.PhaseTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 4/1/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class RuleTemplateResponseDTO {
    private RuleTemplateCategory ruleTemplateCategory;
    private List<PhaseTemplateValue> phaseTemplateValues;
    private Long id;
    private String name;
    private String templateType;
    private String category;
    private Long timeLimit;
    private List<String> balanceType;
    private Boolean checkAgainstTimeRules;
    private Long daysLimit;
    private Long minimumRest;//hh:mm
    private Long daysWorked;
    private Boolean disabled;
    private String description;
    private Long creationDate;
    private Long lastModificationDate;
    private Long nightsWorked;
    private Long intervalLength;
    private String intervalUnit;
    private Long validationStartDateMillis;
    private Boolean balanceAdjustment;
    private Boolean useShiftTimes;
    private Long maximumAvgTime;
    private Double maximumVetoPercentage;
    private Long numberShiftsPerPeriod;
    private Long numberOfWeeks;
    private String fromDayOfWeek;
    private Long fromTime;
    private Boolean proportional;
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
    private int recommendedValue;
    private String lastUpdatedBy;
    private TimeBankTypeEnum frequency;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer yellowZone;
    private Boolean forbid;
    private Boolean allowExtraActivity;
    private List<BreakTemplateValue> breakTemplateValues;


    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(int recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }


    public Long getId() {
        return id;
    }

    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
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

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Boolean getCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(Boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
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

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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

    public Boolean getProportional() {
        return proportional;
    }

    public void setProportional(Boolean proportional) {
        this.proportional = proportional;
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

    public Boolean getOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(Boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
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

    public List<BreakTemplateValue> getBreakTemplateValues() {
        return breakTemplateValues;
    }

    public void setBreakTemplateValues(List<BreakTemplateValue> breakTemplateValues) {
        this.breakTemplateValues = breakTemplateValues;
    }
}
