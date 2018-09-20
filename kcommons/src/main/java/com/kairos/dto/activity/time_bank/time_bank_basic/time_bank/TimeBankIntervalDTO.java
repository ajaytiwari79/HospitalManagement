package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeBankIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private int totalTimeBankAfterCtaMin;
    private int totalTimeBankBeforeCtaMin;
    private int totalScheduledMin;
    private int totalTimeBankMin;
    private int totalContractualMin;
    private int totalTimeBankDiff;
    private String title;
    private List<TimeBankCTADistributionDTO> timeBankDistributions = new ArrayList<>();
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private int minutesFromCta;


    public TimeBankIntervalDTO(String title) {
        this.title = title;
    }


    public int getTotalTimeBankDiff() {
        return totalTimeBankDiff;
    }

    public void setTotalTimeBankDiff(int totalTimeBankDiff) {
        this.totalTimeBankDiff = totalTimeBankDiff;
    }

    public List<TimeBankCTADistributionDTO> getTimeBankDistributions() {
        return timeBankDistributions;
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

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public ScheduleTimeByTimeTypeDTO getNonWorkingTimeType() {
        return nonWorkingTimeType;
    }

    public void setNonWorkingTimeType(ScheduleTimeByTimeTypeDTO nonWorkingTimeType) {
        this.nonWorkingTimeType = nonWorkingTimeType;
    }

    public void setTimeBankDistributions(List<TimeBankCTADistributionDTO> timeBankDistributions) {
        this.timeBankDistributions = timeBankDistributions;
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

    public int getTotalContractualMin() {
        return totalContractualMin;
    }

    public void setTotalContractualMin(int totalContractualMin) {
        this.totalContractualMin = totalContractualMin;
    }
}
