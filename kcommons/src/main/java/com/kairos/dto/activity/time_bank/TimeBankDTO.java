package com.kairos.dto.activity.time_bank;

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

    private long totalTimeBankAfterCtaMin;
    private long totalTimeBankBeforeCtaMin;
    private long totalScheduledMin;
    private long totalTimeBankMin;
    private long totalContractedMin;
    private long totalTimeBankMinLimit;
    private long totalTimeBankMaxLimit;
    private long totalTimeBankInPercent = 10;
    private long totalTimeBankDiff;
    private long paidoutChange;
    private long approvePayOut;
    private long requestPayOut;
    private long totalDeltaBalanceConrection;
    private String phaseName;

    //Distributed min on the basis of Interval;
    private List<TimeBankIntervalDTO> timeIntervals = new ArrayList<>();
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private UnitPositionWithCtaDetailsDTO costTimeAgreement;

    private List<TimeBankIntervalDTO> weeklyIntervalsTimeBank;
    private List<TimeBankIntervalDTO> monthlyIntervalsTimeBank;
    private float hourlyCost;

    public TimeBankDTO(Long unitPositionId, Long staffId, int workingDaysInWeek, int totalWeeklyMins)
     {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMin = totalWeeklyMins;
    }


    public float getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(float hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public long getTotalDeltaBalanceConrection() {
        return totalDeltaBalanceConrection;
    }

    public void setTotalDeltaBalanceConrection(long totalDeltaBalanceConrection) {
        this.totalDeltaBalanceConrection = totalDeltaBalanceConrection;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public TimeBankDTO() {
        this.phaseName = "Total";
    }

    public long getPaidoutChange() {
        return paidoutChange;
    }

    public void setPaidoutChange(long paidoutChange) {
        this.paidoutChange = paidoutChange;
    }

    public long getApprovePayOut() {
        return approvePayOut;
    }

    public void setApprovePayOut(long approvePayOut) {
        this.approvePayOut = approvePayOut;
    }

    public long getRequestPayOut() {
        return requestPayOut;
    }

    public void setRequestPayOut(long requestPayOut) {
        this.requestPayOut = requestPayOut;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getTotalTimeBankAfterCtaMin() {
        return totalTimeBankAfterCtaMin;
    }

    public void setTotalTimeBankAfterCtaMin(long totalTimeBankAfterCtaMin) {
        this.totalTimeBankAfterCtaMin = totalTimeBankAfterCtaMin;
    }

    public long getTotalTimeBankBeforeCtaMin() {
        return totalTimeBankBeforeCtaMin;
    }

    public void setTotalTimeBankBeforeCtaMin(long totalTimeBankBeforeCtaMin) {
        this.totalTimeBankBeforeCtaMin = totalTimeBankBeforeCtaMin;
    }

    public long getTotalScheduledMin() {
        return totalScheduledMin;
    }

    public void setTotalScheduledMin(long totalScheduledMin) {
        this.totalScheduledMin = totalScheduledMin;
    }

    public long getTotalTimeBankMin() {
        return totalTimeBankMin;
    }

    public void setTotalTimeBankMin(long totalTimeBankMin) {
        this.totalTimeBankMin = totalTimeBankMin;
    }

    public long getTotalContractedMin() {
        return totalContractedMin;
    }

    public void setTotalContractedMin(long totalContractedMin) {
        this.totalContractedMin = totalContractedMin;
    }

    public long getTotalTimeBankMinLimit() {
        return totalTimeBankMinLimit;
    }

    public void setTotalTimeBankMinLimit(long totalTimeBankMinLimit) {
        this.totalTimeBankMinLimit = totalTimeBankMinLimit;
    }

    public long getTotalTimeBankMaxLimit() {
        return totalTimeBankMaxLimit;
    }

    public void setTotalTimeBankMaxLimit(long totalTimeBankMaxLimit) {
        this.totalTimeBankMaxLimit = totalTimeBankMaxLimit;
    }

    public long getTotalTimeBankInPercent() {
        return totalTimeBankInPercent;
    }

    public void setTotalTimeBankInPercent(long totalTimeBankInPercent) {
        this.totalTimeBankInPercent = totalTimeBankInPercent;
    }

    public long getTotalTimeBankDiff() {
        return totalTimeBankDiff;
    }

    public void setTotalTimeBankDiff(long totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }

    public TimeBankCTADistributionDTO getTimeBankDistribution() {
        return timeBankDistribution;
    }

    public void setTimeBankDistribution(TimeBankCTADistributionDTO timeBankDistribution) {
        this.timeBankDistribution = timeBankDistribution;
    }

    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(UnitPositionWithCtaDetailsDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
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
