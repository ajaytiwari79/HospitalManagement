package com.kairos.activity.persistence.model.wta;

import com.kairos.activity.persistence.model.tag.Tag;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.response.dto.web.OrganizationTypeDTO;
import com.kairos.response.dto.web.experties.ExpertiseResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 13/4/18
 */

public class WTAQueryResultDTO {

    private WTAQueryResultDTO parentWTA;

    private WTAQueryResultDTO countryParentWTA;

    private WTAQueryResultDTO organizationParentWTA;


    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private String name;
    private String description;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;

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

    private List<Tag> tags = new ArrayList<>();


    public WTAQueryResultDTO getCountryParentWTA() {
        return countryParentWTA;
    }

    public void setCountryParentWTA(WTAQueryResultDTO countryParentWTA) {
        this.countryParentWTA = countryParentWTA;
    }

    public WTAQueryResultDTO getOrganizationParentWTA() {
        return organizationParentWTA;
    }

    public void setOrganizationParentWTA(WTAQueryResultDTO organizationParentWTA) {
        this.organizationParentWTA = organizationParentWTA;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public WTAQueryResultDTO getParentWTA() {
        return parentWTA;
    }

    public void setParentWTA(WTAQueryResultDTO parentWTA) {
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public ExpertiseResponseDTO getExpertise() {
        return expertise;
    }

    public void setExpertise(ExpertiseResponseDTO expertise) {
        this.expertise = expertise;
    }

    public OrganizationTypeDTO getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeDTO organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationTypeDTO getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationTypeDTO organizationSubType) {
        this.organizationSubType = organizationSubType;
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
}
