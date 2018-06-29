package com.kairos.activity.pay_out;

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
public class PayOutDTO {

    private Long unitPositionId;
    private Long staffId;
    private int workingDaysInWeek;
    private int totalWeeklyMin;
    private Date startDate;
    private Date endDate;
    private Long unitId;
    private String query;

    private int totalPayOutAfterCtaMin;
    private int totalPayOutBeforeCtaMin;
    private int totalScheduledMin;
    private int totalPayOutMin;
    private int totalContractedMin;
    private int totalPayOutMinLimit;
    private int totalPayOutMaxLimit;
    private int totalPayOutInPercent = 10;
    private int totalPayOutDiff;

    //Distributed min on the basis of Interval;
    private List<PayOutIntervalDTO> timeIntervals = new ArrayList<>();
    private List<PayOutCTADistributionDTO> payOutDistributions = new ArrayList<>();
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private UnitPositionWithCtaDetailsDTO costTimeAgreement;

    private List<PayOutIntervalDTO> weeklyIntervalsPayOut;
    private List<PayOutIntervalDTO> monthlyIntervalsPayOut;

    public PayOutDTO(Long unitPositionId, Long staffId, int workingDaysInWeek, int totalWeeklyMins)
     {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMin = totalWeeklyMins;
    }

    public int getTotalPayOutDiff() {
        return totalPayOutDiff;
    }

    public void setTotalPayOutDiff(int totalPayOutDiff) {
        this.totalPayOutDiff = totalPayOutDiff;
    }

    public int getTotalPayOutInPercent() {
        return totalPayOutInPercent;
    }

    public void setTotalPayOutInPercent(int totalPayOutInPercent) {
        this.totalPayOutInPercent = totalPayOutInPercent;
    }

    public int getTotalPayOutMinLimit() {
        return totalPayOutMinLimit;
    }

    public void setTotalPayOutMinLimit(int totalPayOutMinLimit) {
        this.totalPayOutMinLimit = totalPayOutMinLimit;
    }

    public int getTotalPayOutMaxLimit() {
        return totalPayOutMaxLimit;
    }

    public void setTotalPayOutMaxLimit(int totalPayOutMaxLimit) {
        this.totalPayOutMaxLimit = totalPayOutMaxLimit;
    }

    public List<PayOutIntervalDTO> getWeeklyIntervalsPayOut() {
        return weeklyIntervalsPayOut;
    }

    public void setWeeklyIntervalsPayOut(List<PayOutIntervalDTO> weeklyIntervalsPayOut) {
        this.weeklyIntervalsPayOut = weeklyIntervalsPayOut;
    }

    public List<PayOutIntervalDTO> getMonthlyIntervalsPayOut() {
        return monthlyIntervalsPayOut;
    }

    public void setMonthlyIntervalsPayOut(List<PayOutIntervalDTO> monthlyIntervalsPayOut) {
        this.monthlyIntervalsPayOut = monthlyIntervalsPayOut;
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

    public List<PayOutCTADistributionDTO> getPayOutDistributions() {
        return payOutDistributions;
    }

    public void setPayOutDistributions(List<PayOutCTADistributionDTO> payOutDistributions) {
        this.payOutDistributions = payOutDistributions;
    }

    public PayOutDTO() {
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

    public int getTotalPayOutAfterCtaMin() {
        return totalPayOutAfterCtaMin;
    }

    public void setTotalPayOutAfterCtaMin(int totalPayOutAfterCtaMin) {
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
    }

    public int getTotalPayOutBeforeCtaMin() {
        return totalPayOutBeforeCtaMin;
    }

    public void setTotalPayOutBeforeCtaMin(int totalPayOutBeforeCtaMin) {
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
    }

    public int getTotalScheduledMin() {
        return totalScheduledMin;
    }

    public void setTotalScheduledMin(int totalScheduledMin) {
        this.totalScheduledMin = totalScheduledMin;
    }

    public int getTotalPayOutMin() {
        return totalPayOutMin;
    }

    public void setTotalPayOutMin(int totalPayOutMin) {
        this.totalPayOutMin = totalPayOutMin;
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

    public List<PayOutIntervalDTO> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(List<PayOutIntervalDTO> timeIntervals) {
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
