package com.kairos.activity.persistence.model.wta;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * @Author vipul
 *
 * @Modified added organization and staff for personal copy
 */
@Document
public class WorkingTimeAgreement extends MongoBaseEntity {

    @NotNull(message = "error.WorkingTimeAgreement.name.notnull")
    private String name;

    private String description;
    // This will be only used when the countryId will update the WTA a new Copy of WTA will be assigned to organization having state disabled
    private Boolean disabled;

    private WTAExpertise expertise;

    private WTAOrganizationType organizationType;

    private WTAOrganizationType organizationSubType;


    private Long countryId;

    private WTAOrganization organization;

    private List<ShiftLengthWTATemplate> shiftLengths = new ArrayList<>();
    private List<AverageScheduledTimeWTATemplate> averageScheduledTimes = new ArrayList<>();
    private List<CareDayCheckWTATemplate> careDayChecks = new ArrayList<>();
    private List<ConsecutiveRestPartOfDayWTATemplate> consecutiveRestPartOfDays = new ArrayList<>();
    private List<ConsecutiveWorkWTATemplate> consecutiveWorks = new ArrayList<>();
    private List<DailyRestingTimeWTATemplate> dailyRestingTimes = new ArrayList<>();
    private List<DaysOffInPeriodWTATemplate> daysOffInPeriods = new ArrayList<>();
    private List<DurationBetweenShiftWTATemplate> durationBetweenShifts = new ArrayList<>();
    private List<NumberOfPartOfDayShiftsWTATemplate> numberOfPartOfDayShifts = new ArrayList<>();
    private List<NumberOfWeekendShiftInPeriodWTATemplate> numberOfWeekendShiftInPeriods = new ArrayList<>();
    private List<SeniorDaysInYearWTATemplate> seniorDaysInYears = new ArrayList<>();
    private List<ShiftsInIntervalWTATemplate> shiftsInIntervals = new ArrayList<>();
    private List<ShortestAndAverageDailyRestWTATemplate> shortestAndAverageDailyRests = new ArrayList<>();
    private List<TimeBankWTATemplate> timeBanks = new ArrayList<>();
    private List<VetoPerPeriodWTATemplate> vetoPerPeriods = new ArrayList<>();
    private List<WeeklyRestPeriodWTATemplate> weeklyRestPeriods = new ArrayList<>();




    // to make a history
    private BigInteger parentWTA;

    private BigInteger countryParentWTA;

    private BigInteger organizationParentWTA;


    private List<BigInteger> tags = new ArrayList<>();

    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;

    public WTAOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(WTAOrganization organization) {
        this.organization = organization;
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

    public WTAExpertise getExpertise() {
        return expertise;
    }

    public void setExpertise(WTAExpertise expertise) {
        this.expertise = expertise;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public BigInteger getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(BigInteger parentWTA) {
        this.parentWTA = parentWTA;
    }


    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public WTAOrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(WTAOrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public WTAOrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(WTAOrganizationType organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public BigInteger getCountryParentWTA() {
        return countryParentWTA;
    }

    public void setCountryParentWTA(BigInteger countryParentWTA) {
        this.countryParentWTA = countryParentWTA;
    }

    public BigInteger getOrganizationParentWTA() {
        return organizationParentWTA;
    }

    public void setOrganizationParentWTA(BigInteger organizationParentWTA) {
        this.organizationParentWTA = organizationParentWTA;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public List<ShiftLengthWTATemplate> getShiftLengths() {
        return shiftLengths;
    }

    public void setShiftLengths(List<ShiftLengthWTATemplate> shiftLengths) {
        this.shiftLengths = shiftLengths;
    }

    public List<AverageScheduledTimeWTATemplate> getAverageScheduledTimes() {
        return averageScheduledTimes;
    }

    public void setAverageScheduledTimes(List<AverageScheduledTimeWTATemplate> averageScheduledTimes) {
        this.averageScheduledTimes = averageScheduledTimes;
    }

    public List<CareDayCheckWTATemplate> getCareDayChecks() {
        return careDayChecks;
    }

    public void setCareDayChecks(List<CareDayCheckWTATemplate> careDayChecks) {
        this.careDayChecks = careDayChecks;
    }

    public List<ConsecutiveRestPartOfDayWTATemplate> getConsecutiveRestPartOfDays() {
        return consecutiveRestPartOfDays;
    }

    public void setConsecutiveRestPartOfDays(List<ConsecutiveRestPartOfDayWTATemplate> consecutiveRestPartOfDays) {
        this.consecutiveRestPartOfDays = consecutiveRestPartOfDays;
    }

    public List<ConsecutiveWorkWTATemplate> getConsecutiveWorks() {
        return consecutiveWorks;
    }

    public void setConsecutiveWorks(List<ConsecutiveWorkWTATemplate> consecutiveWorks) {
        this.consecutiveWorks = consecutiveWorks;
    }

    public List<DailyRestingTimeWTATemplate> getDailyRestingTimes() {
        return dailyRestingTimes;
    }

    public void setDailyRestingTimes(List<DailyRestingTimeWTATemplate> dailyRestingTimes) {
        this.dailyRestingTimes = dailyRestingTimes;
    }

    public List<DaysOffInPeriodWTATemplate> getDaysOffInPeriods() {
        return daysOffInPeriods;
    }

    public void setDaysOffInPeriods(List<DaysOffInPeriodWTATemplate> daysOffInPeriods) {
        this.daysOffInPeriods = daysOffInPeriods;
    }

    public List<DurationBetweenShiftWTATemplate> getDurationBetweenShifts() {
        return durationBetweenShifts;
    }

    public void setDurationBetweenShifts(List<DurationBetweenShiftWTATemplate> durationBetweenShifts) {
        this.durationBetweenShifts = durationBetweenShifts;
    }

    public List<NumberOfPartOfDayShiftsWTATemplate> getNumberOfPartOfDayShifts() {
        return numberOfPartOfDayShifts;
    }

    public void setNumberOfPartOfDayShifts(List<NumberOfPartOfDayShiftsWTATemplate> numberOfPartOfDayShifts) {
        this.numberOfPartOfDayShifts = numberOfPartOfDayShifts;
    }

    public List<NumberOfWeekendShiftInPeriodWTATemplate> getNumberOfWeekendShiftInPeriods() {
        return numberOfWeekendShiftInPeriods;
    }

    public void setNumberOfWeekendShiftInPeriods(List<NumberOfWeekendShiftInPeriodWTATemplate> numberOfWeekendShiftInPeriods) {
        this.numberOfWeekendShiftInPeriods = numberOfWeekendShiftInPeriods;
    }

    public List<SeniorDaysInYearWTATemplate> getSeniorDaysInYears() {
        return seniorDaysInYears;
    }

    public void setSeniorDaysInYears(List<SeniorDaysInYearWTATemplate> seniorDaysInYears) {
        this.seniorDaysInYears = seniorDaysInYears;
    }

    public List<ShiftsInIntervalWTATemplate> getShiftsInIntervals() {
        return shiftsInIntervals;
    }

    public void setShiftsInIntervals(List<ShiftsInIntervalWTATemplate> shiftsInIntervals) {
        this.shiftsInIntervals = shiftsInIntervals;
    }

    public List<ShortestAndAverageDailyRestWTATemplate> getShortestAndAverageDailyRests() {
        return shortestAndAverageDailyRests;
    }

    public void setShortestAndAverageDailyRests(List<ShortestAndAverageDailyRestWTATemplate> shortestAndAverageDailyRests) {
        this.shortestAndAverageDailyRests = shortestAndAverageDailyRests;
    }

    public List<TimeBankWTATemplate> getTimeBanks() {
        return timeBanks;
    }

    public void setTimeBanks(List<TimeBankWTATemplate> timeBanks) {
        this.timeBanks = timeBanks;
    }

    public List<VetoPerPeriodWTATemplate> getVetoPerPeriods() {
        return vetoPerPeriods;
    }

    public void setVetoPerPeriods(List<VetoPerPeriodWTATemplate> vetoPerPeriods) {
        this.vetoPerPeriods = vetoPerPeriods;
    }

    public List<WeeklyRestPeriodWTATemplate> getWeeklyRestPeriods() {
        return weeklyRestPeriods;
    }

    public void setWeeklyRestPeriods(List<WeeklyRestPeriodWTATemplate> weeklyRestPeriods) {
        this.weeklyRestPeriods = weeklyRestPeriods;
    }

    public WorkingTimeAgreement(BigInteger id, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description, Long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }

    public WorkingTimeAgreement() {
        //default
    }
    public WorkingTimeAgreement basicDetails() {
        return new WorkingTimeAgreement(this.id, this.name, this.description, this.startDateMillis, this.endDateMillis, this.expiryDate);
    }


}
