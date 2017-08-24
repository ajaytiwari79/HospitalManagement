package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by pawanmandhan on 10/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class WTABaseRuleTemplateDTO {

    private String name;
    private String templateType;
    private Long id;
    private RuleTemplateCategory ruleTemplateCategory;
    private Boolean isActive;
    private String description;
    private Long daysWorked;
    private Long number;
    private Long creationDate;
    private Long lastModificationDate;
    private String time;
    private Long days;//no of days
    private String minimumRest;
    private Boolean checkAgainstTimeRules;
    private Long nightsWorked;
    private Long minimumDaysOff;
    private Boolean balanceAdjustment;
    private Boolean calculatedShift;
    private String maximumAvgTime;
    private Long maximumVeto;
    private Long numberShiftsPerPeriod;
    private Long numberOfWeeks;
    private String fromDayOfWeek; //(day of week)
    private Long fromTime;
    private Long proportional;
    private String minimumDurationBetweenShifts;
    private String continuousWeekRest;
    private String continuousDayRestHours;
    private String averageRest;//(hours number)
    private String shiftAffiliation;//(List checkbox)
    private List<String> balanceType;//multiple check boxes
    private Boolean onlyCompositeShifts;//(checkbox)
    private Long interval;//
    private String intervalUnit;
    private Long validationStartDate;
    private String activityCode;// checkbox)


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
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

    public Long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(Long daysWorked) {
        this.daysWorked = daysWorked;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public String getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(String minimumRest) {
        this.minimumRest = minimumRest;
    }

    public Boolean getCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(Boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public Long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(Long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public Long getMinimumDaysOff() {
        return minimumDaysOff;
    }

    public void setMinimumDaysOff(Long minimumDaysOff) {
        this.minimumDaysOff = minimumDaysOff;
    }

    public Boolean getBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(Boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public Boolean getCalculatedShift() {
        return calculatedShift;
    }

    public void setCalculatedShift(Boolean calculatedShift) {
        this.calculatedShift = calculatedShift;
    }

    public String getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(String maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public Long getMaximumVeto() {
        return maximumVeto;
    }

    public void setMaximumVeto(Long maximumVeto) {
        this.maximumVeto = maximumVeto;
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

    public Long getProportional() {
        return proportional;
    }

    public void setProportional(Long proportional) {
        this.proportional = proportional;
    }

    public String getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(String minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public String getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(String continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public String getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(String continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public String getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(String averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public Boolean getOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(Boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public Long getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(Long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
}
