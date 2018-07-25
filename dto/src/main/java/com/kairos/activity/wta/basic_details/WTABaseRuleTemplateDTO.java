package com.kairos.activity.wta.basic_details;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.activity.wta.templates.*;
import com.kairos.enums.WTATemplateType;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by Pradeep on 27/4/18.
 */
//@JsonDeserialize(using = JsonDeserializer.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "wtaTemplateType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AverageScheduledTimeWTATemplateDTO.class, name = "AVERAGE_SHEDULED_TIME"),
        @JsonSubTypes.Type(value = ConsecutiveWorkWTATemplateDTO.class, name = "CONSECUTIVE_WORKING_PARTOFDAY"),
        @JsonSubTypes.Type(value = DaysOffInPeriodWTATemplateDTO.class, name = "DAYS_OFF_IN_PERIOD"),
        @JsonSubTypes.Type(value = NumberOfPartOfDayShiftsWTATemplateDTO.class, name = "NUMBER_OF_PARTOFDAY"),
        @JsonSubTypes.Type(value = ShiftLengthWTATemplateDTO.class, name = "SHIFT_LENGTH"),
        @JsonSubTypes.Type(value = ShiftsInIntervalWTATemplateDTO.class, name = "NUMBER_OF_SHIFTS_IN_INTERVAL"),
        @JsonSubTypes.Type(value = TimeBankWTATemplateDTO.class, name = "TIME_BANK"),
        @JsonSubTypes.Type(value = VetoPerPeriodWTATemplateDTO.class, name = "VETO_PER_PERIOD"),
        @JsonSubTypes.Type(value = DurationBetweenShiftsWTATemplateDTO.class, name = "DURATION_BETWEEN_SHIFTS"),
        @JsonSubTypes.Type(value = ConsecutiveRestPartOfDayWTATemplateDTO.class, name = "REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS"),
        @JsonSubTypes.Type(value = WeeklyRestPeriodWTATemplateDTO.class, name = "WEEKLY_REST_PERIOD"),
        @JsonSubTypes.Type(value = NumberOfWeekendShiftsInPeriodWTATemplateDTO.class, name = "NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD"),
        @JsonSubTypes.Type(value = ShortestAndAverageDailyRestWTATemplateDTO.class, name = "SHORTEST_AND_AVERAGE_DAILY_REST"),
        @JsonSubTypes.Type(value = SeniorDaysPerYearWTATemplateDTO.class, name = "SENIOR_DAYS_PER_YEAR"),
        @JsonSubTypes.Type(value = ChildCareDaysCheckWTATemplateDTO.class, name = "CHILD_CARE_DAYS_CHECK"),
        @JsonSubTypes.Type(value = DaysOffAfterASeriesWTATemplateDTO.class, name = "DAYS_OFF_AFTER_A_SERIES"),
        @JsonSubTypes.Type(value = NoOfSequenceShiftWTATemplateDTO.class, name = "NO_OF_SEQUENCE_SHIFT"),
        @JsonSubTypes.Type(value = EmployeesWithIncreasedRiskWTATemplateDTO.class,name="EMPLOYEES_WITH_INCREASE_RISK")
})
public class WTABaseRuleTemplateDTO{

    protected BigInteger id;
    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger ruleTemplateCategoryId;
    protected String lastUpdatedBy;
    protected Long countryId;

    protected RuleTemplateCategoryDTO ruleTemplateCategory;
    protected WTATemplateType wtaTemplateType;

    protected Integer staffCanIgnoreCounter;
    protected Integer managementCanIgnoreCounter;
    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    protected List<PhaseTemplateValue> phaseTemplateValues;

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public BigInteger getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategoryId(BigInteger ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public WTABaseRuleTemplateDTO(){}

    public WTABaseRuleTemplateDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Integer getStaffCanIgnoreCounter() {
        return staffCanIgnoreCounter;
    }

    public void setStaffCanIgnoreCounter(Integer staffCanIgnoreCounter) {
        this.staffCanIgnoreCounter = staffCanIgnoreCounter;
    }

    public Integer getManagementCanIgnoreCounter() {
        return managementCanIgnoreCounter;
    }

    public void setManagementCanIgnoreCounter(Integer managementCanIgnoreCounter) {
        this.managementCanIgnoreCounter = managementCanIgnoreCounter;
    }
}
