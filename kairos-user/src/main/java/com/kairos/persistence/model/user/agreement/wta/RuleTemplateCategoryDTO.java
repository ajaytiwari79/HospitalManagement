package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.PhaseTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 12/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class RuleTemplateCategoryDTO {
    private List<PhaseTemplateValue> phaseTemplateValues;
    //private List<Map> phaseTemplateValue;

    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }

    private RuleTemplateCategory ruleTemplateCategory;
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


    public int getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(int recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

//    public List<PhaseTemplateValue> getPhaseTemplateValues() {
//        return phaseTemplateValues;
//    }
//
//    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
//        this.phaseTemplateValues = phaseTemplateValues;
//    }

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

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

    public Long getId() {
        return id;
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

//    public List<Map> getPhaseTemplateValue() {
//        return phaseTemplateValues;
//    }
//
//    public void setPhaseTemplateValue(List<Map> phaseTemplateValue) {
//        this.phaseTemplateValues = phaseTemplateValues;
//    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ruleTemplateCategory", ruleTemplateCategory)
                .append("id", id)
                .append("name", name)
                .append("templateType", templateType)
                .append("category", category)
                .append("timeLimit", timeLimit)
                .append("balanceType", balanceType)
                .append("checkAgainstTimeRules", checkAgainstTimeRules)
                .append("daysLimit", daysLimit)
                .append("minimumRest", minimumRest)
                .append("daysWorked", daysWorked)
                .append("disabled", disabled)
                .append("description", description)
                .append("creationDate", creationDate)
                .append("lastModificationDate", lastModificationDate)
                .append("nightsWorked", nightsWorked)
                .append("intervalLength", intervalLength)
                .append("intervalUnit", intervalUnit)
                .append("validationStartDateMillis", validationStartDateMillis)
                .append("balanceAdjustment", balanceAdjustment)
                .append("useShiftTimes", useShiftTimes)
                .append("maximumAvgTime", maximumAvgTime)
                .append("maximumVetoPercentage", maximumVetoPercentage)
                .append("numberShiftsPerPeriod", numberShiftsPerPeriod)
                .append("numberOfWeeks", numberOfWeeks)
                .append("fromDayOfWeek", fromDayOfWeek)
                .append("fromTime", fromTime)
                .append("proportional", proportional)
                .append("toTime", toTime)
                .append("toDayOfWeek", toDayOfWeek)
                .append("continuousDayRestHours", continuousDayRestHours)
                .append("minimumDurationBetweenShifts", minimumDurationBetweenShifts)
                .append("continuousWeekRest", continuousWeekRest)
                .append("averageRest", averageRest)
                .append("shiftAffiliation", shiftAffiliation)
                .append("shiftsLimit", shiftsLimit)
                .append("onlyCompositeShifts", onlyCompositeShifts)
                .append("activityCode", activityCode)
                .toString();
    }

    public RuleTemplateCategoryDTO() {
        //default const
    }

    public RuleTemplateCategoryDTO(String name, String templateType, boolean disabled, String description, long timeInMins, List<String> balanceTypes, boolean checkAgainstTimeRules) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.timeLimit = timeInMins;
        this.balanceType = balanceTypes;
        this.checkAgainstTimeRules = checkAgainstTimeRules;

    }

    // Template 13 Cons
    public RuleTemplateCategoryDTO(String name, String templateType, Boolean disabled, String description, Long numberShiftsPerPeriod, Long numberOfWeeks,
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

    public RuleTemplateCategoryDTO(String name, String templateType, Boolean disabled,
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
    public RuleTemplateCategoryDTO(String name, String templateType, Boolean disabled, String description, Long continuousDayRestHours) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.continuousDayRestHours = continuousDayRestHours;
    }

}
