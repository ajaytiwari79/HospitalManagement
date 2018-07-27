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
    private long totalContractedMin;
    private long totalTimeBankDiff;
    private long paidoutChange;
    private long approvePayOut;
    private long requestPayOut;
    private long totalDeltaBalanceConrection;
    private String phaseName;
    private String title;
    private TimeBankCTADistributionDTO timeBankDistribution;
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private String headerName;


    public TimeBankCTADistributionDTO getTimeBankDistribution() {
        return timeBankDistribution;
    }

    public void setTimeBankDistribution(TimeBankCTADistributionDTO timeBankDistribution) {
        this.timeBankDistribution = timeBankDistribution;
    }

    public TimeBankIntervalDTO(String title) {
        this.title = title;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public void setTotalContractedMin(long totalContractedMin) {
        this.totalContractedMin = totalContractedMin;
    }

    public long getTotalDeltaBalanceConrection() {
        return totalDeltaBalanceConrection;
    }

    public void setTotalDeltaBalanceConrection(long totalDeltaBalanceConrection) {
        this.totalDeltaBalanceConrection = totalDeltaBalanceConrection;
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

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
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
        this.phaseName = "T & A Phase";
    }

    public TimeBankIntervalDTO(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.phaseName = "T & A Phase";
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

    public void setTotalContractedMin(int totalContractedMin) {
        this.totalContractedMin = totalContractedMin;
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

    public void setTotalContractualMin(long totalContractualMin) {
        this.totalContractedMin = totalContractualMin;
    }

    public long getTotalTimeBankDiff() {
        return totalTimeBankDiff;
    }

    public void setTotalTimeBankDiff(long totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }
}
