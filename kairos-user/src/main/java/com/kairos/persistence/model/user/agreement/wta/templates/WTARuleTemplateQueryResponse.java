package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by pawanmandhan on 25/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class WTARuleTemplateQueryResponse {

    private Long id;
    private String name;
    private String templateType;
    private RuleTemplateCategory ruleTemplateCategory;
    private Long timeLimit;
    private List<String> balanceType;
    private Boolean checkAgainstTimeRules;
    private Long daysLimit;
    private Long minimumRest;//hh:mm
    private Long daysWorked;
    private Boolean isActive;
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


    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
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

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public Long getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(Long averageRest) {
        this.averageRest = averageRest;
    }

    public Long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(Long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public Long getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(Long minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public Long getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(Long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
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

    public Double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(Double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
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

    public Long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(Long nightsWorked) {
        this.nightsWorked = nightsWorked;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
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

    public Boolean getCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public Long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(Long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public void setCheckAgainstTimeRules(Boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
