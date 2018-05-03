package com.kairos.response.dto.pay_out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayOutIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private int totalPayOutAfterCtaMin;
    private int totalPayOutBeforeCtaMin;
    private int totalScheduledMin;
    private int totalPayOutMin;
    private int totalContractualMin;
    private int totalPayOutDiff;
    private String title;
    private List<PayOutCTADistributionDTO> payOutDistributions = new ArrayList<>();
    private ScheduleTimeByTimeTypeDTO workingTimeType;
    private ScheduleTimeByTimeTypeDTO nonWorkingTimeType;
    private int minutesFromCta;


    public PayOutIntervalDTO(String title) {
        this.title = title;
    }


    public int getTotalPayOutDiff() {
        return totalPayOutDiff;
    }

    public void setTotalPayOutDiff(int totalPayOutDiff) {
        this.totalPayOutDiff = totalPayOutDiff;
    }

    public List<PayOutCTADistributionDTO> getPayOutDistributions() {
        return payOutDistributions;
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

    public void setPayOutDistributions(List<PayOutCTADistributionDTO> payOutDistributions) {
        this.payOutDistributions = payOutDistributions;
    }

    public PayOutIntervalDTO() {
    }

    public PayOutIntervalDTO(Date startDate, Date endDate) {
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

    public int getTotalContractualMin() {
        return totalContractualMin;
    }

    public void setTotalContractualMin(int totalContractualMin) {
        this.totalContractualMin = totalContractualMin;
    }
}
