package com.kairos.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.DayOfWeek;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeBankIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalTimeBankAfterCtaMin;
    private long totalTimeBankBeforeCtaMin;
    private long totalScheduledMin;
    private long totalTimeBankMin;
    private long totalContractualMin;
    private long totalTimeBankDiff;
    private long paidoutChange;
    private long approvePayOut;
    private long requestPayOut;
    private String title;
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private DayOfWeek dayOfWeek;


    public TimeBankCTADistributionDTO getTimeBankDistribution() {
        return timeBankDistribution;
    }

    public void setTimeBankDistribution(TimeBankCTADistributionDTO timeBankDistribution) {
        this.timeBankDistribution = timeBankDistribution;
    }

    public TimeBankIntervalDTO(String title) {
        this.title = title;
    }



    public void setTotalTimeBankDiff(int totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ScheduleTimeByTimeTypeDTO getWorkingTimeType() {
        return workingTimeType;
    }

    public void setWorkingTimeType(ScheduleTimeByTimeTypeDTO workingTimeType) {
        this.workingTimeType = workingTimeType;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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


    public ScheduleTimeByTimeTypeDTO getNonWorkingTimeType() {
        return nonWorkingTimeType;
    }

    public void setNonWorkingTimeType(ScheduleTimeByTimeTypeDTO nonWorkingTimeType) {
        this.nonWorkingTimeType = nonWorkingTimeType;
    }


    public TimeBankIntervalDTO() {
    }

    public TimeBankIntervalDTO(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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


    public void setTotalTimeBankAfterCtaMin(int totalTimeBankAfterCtaMin) {
        this.totalTimeBankAfterCtaMin = totalTimeBankAfterCtaMin;
    }

    public void setTotalTimeBankBeforeCtaMin(int totalTimeBankBeforeCtaMin) {
        this.totalTimeBankBeforeCtaMin = totalTimeBankBeforeCtaMin;
    }

    public void setTotalScheduledMin(int totalScheduledMin) {
        this.totalScheduledMin = totalScheduledMin;
    }

    public void setTotalTimeBankMin(int totalTimeBankMin) {
        this.totalTimeBankMin = totalTimeBankMin;
    }

    public void setTotalContractualMin(int totalContractualMin) {
        this.totalContractualMin = totalContractualMin;
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

    public long getTotalContractualMin() {
        return totalContractualMin;
    }

    public void setTotalContractualMin(long totalContractualMin) {
        this.totalContractualMin = totalContractualMin;
    }

    public long getTotalTimeBankDiff() {
        return totalTimeBankDiff;
    }

    public void setTotalTimeBankDiff(long totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }
}
