package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeBankDTO {

    private Long unitPositionId;
    private Long staffId;
    private int workingDaysInWeek;
    private int totalWeeklyMin;
    private Date startDate;
    private Date endDate;
    private Long unitId;
    private String query;

    private int totalTimeBankAfterCtaMin;
    private int totalTimeBankBeforeCtaMin;
    private int totalScheduledMin;
    private int totalTimeBankMin;
    private int totalContractedMin;
    private int totalTimeBankMinLimit;
    private int totalTimeBankMaxLimit;
    private int totalTimeBankInPercent = 10;
    private int totalTimeBankDiff;
    private int minutesFromCta;

    //Distributed min on the basis of Interval;
    private List<TimeBankIntervalDTO> timeIntervals = new ArrayList<>();
    private List<TimeBankCTADistributionDTO> timeBankDistributions = new ArrayList<>();
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private UnitPositionWithCtaDetailsDTO costTimeAgreement;

    private List<TimeBankIntervalDTO> weeklyIntervalsTimeBank;
    private List<TimeBankIntervalDTO> monthlyIntervalsTimeBank;

    public TimeBankDTO(Long unitPositionId, Long staffId, int workingDaysInWeek, int totalWeeklyMins)
     {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMin = totalWeeklyMins;
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public int getTotalTimeBankDiff() {
        return totalTimeBankDiff;
    }

    public void setTotalTimeBankDiff(int totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }

    public int getTotalTimeBankInPercent() {
        return totalTimeBankInPercent;
    }

    public void setTotalTimeBankInPercent(int totalTimeBankInPercent) {
        this.totalTimeBankInPercent = totalTimeBankInPercent;
    }

    public int getTotalTimeBankMinLimit() {
        return totalTimeBankMinLimit;
    }

    public void setTotalTimeBankMinLimit(int totalTimeBankMinLimit) {
        this.totalTimeBankMinLimit = totalTimeBankMinLimit;
    }

    public int getTotalTimeBankMaxLimit() {
        return totalTimeBankMaxLimit;
    }

    public void setTotalTimeBankMaxLimit(int totalTimeBankMaxLimit) {
        this.totalTimeBankMaxLimit = totalTimeBankMaxLimit;
    }

    public List<TimeBankIntervalDTO> getWeeklyIntervalsTimeBank() {
        return weeklyIntervalsTimeBank;
    }

    public void setWeeklyIntervalsTimeBank(List<TimeBankIntervalDTO> weeklyIntervalsTimeBank) {
        this.weeklyIntervalsTimeBank = weeklyIntervalsTimeBank;
    }

    public List<TimeBankIntervalDTO> getMonthlyIntervalsTimeBank() {
        return monthlyIntervalsTimeBank;
    }

    public void setMonthlyIntervalsTimeBank(List<TimeBankIntervalDTO> monthlyIntervalsTimeBank) {
        this.monthlyIntervalsTimeBank = monthlyIntervalsTimeBank;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(UnitPositionWithCtaDetailsDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public List<TimeBankCTADistributionDTO> getTimeBankDistributions() {
        return timeBankDistributions;
    }

    public void setTimeBankDistributions(List<TimeBankCTADistributionDTO> timeBankDistributions) {
        this.timeBankDistributions = timeBankDistributions;
    }

    public TimeBankDTO() {
    }

    public ScheduleTimeByTimeTypeDTO getWorkingTimeType() {
        return workingTimeType;
    }

    public void setWorkingTimeType(ScheduleTimeByTimeTypeDTO workingTimeType) {
        this.workingTimeType = workingTimeType;
    }

    public ScheduleTimeByTimeTypeDTO getNonWorkingTimeType() {
        return nonWorkingTimeType;
    }

    public void setNonWorkingTimeType(ScheduleTimeByTimeTypeDTO nonWorkingTimeType) {
        this.nonWorkingTimeType = nonWorkingTimeType;
    }

    public int getTotalTimeBankAfterCtaMin() {
        return totalTimeBankAfterCtaMin;
    }

    public void setTotalTimeBankAfterCtaMin(int totalTimeBankAfterCtaMin) {
        this.totalTimeBankAfterCtaMin = totalTimeBankAfterCtaMin;
    }

    public int getTotalTimeBankBeforeCtaMin() {
        return totalTimeBankBeforeCtaMin;
    }

    public void setTotalTimeBankBeforeCtaMin(int totalTimeBankBeforeCtaMin) {
        this.totalTimeBankBeforeCtaMin = totalTimeBankBeforeCtaMin;
    }

    public int getTotalScheduledMin() {
        return totalScheduledMin;
    }

    public void setTotalScheduledMin(int totalScheduledMin) {
        this.totalScheduledMin = totalScheduledMin;
    }

    public int getTotalTimeBankMin() {
        return totalTimeBankMin;
    }

    public void setTotalTimeBankMin(int totalTimeBankMin) {
        this.totalTimeBankMin = totalTimeBankMin;
    }

    public int getTotalContractedMin() {
        return totalContractedMin;
    }

    public void setTotalContractedMin(int totalContractedMin) {
        this.totalContractedMin = totalContractedMin;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<TimeBankIntervalDTO> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(List<TimeBankIntervalDTO> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }


    public int getTotalWeeklyMin() {
        return totalWeeklyMin;
    }

    public void setTotalWeeklyMin(int totalWeeklyMin) {
        this.totalWeeklyMin = totalWeeklyMin;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }


    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}
