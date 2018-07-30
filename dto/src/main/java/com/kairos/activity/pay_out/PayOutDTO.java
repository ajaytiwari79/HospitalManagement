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

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalPayOutAfterCtaMin;
    private long totalPayOutBeforeCtaMin;
    private long payOutChange;

    //Distributed min on the basis of Interval;
    private List<PayOutIntervalDTO> timeIntervals = new ArrayList<>();
    private PayOutCTADistributionDTO payOutDistribution;


    public PayOutDTO() {
    }

    public PayOutDTO(Date startDate, Date endDate, long totalPayOutAfterCtaMin, long totalPayOutBeforeCtaMin,long payOutChange, List<PayOutIntervalDTO> timeIntervals, PayOutCTADistributionDTO payOutDistribution) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
        this.payOutChange = payOutChange;
        this.timeIntervals = timeIntervals;
        this.payOutDistribution = payOutDistribution;
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

    public long getTotalPayOutAfterCtaMin() {
        return totalPayOutAfterCtaMin;
    }

    public void setTotalPayOutAfterCtaMin(long totalPayOutAfterCtaMin) {
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
    }

    public long getTotalPayOutBeforeCtaMin() {
        return totalPayOutBeforeCtaMin;
    }

    public void setTotalPayOutBeforeCtaMin(long totalPayOutBeforeCtaMin) {
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
    }

    public long getPayOutChange() {
        return payOutChange;
    }

    public void setPayOutChange(long payOutChange) {
        this.payOutChange = payOutChange;
    }


    public List<PayOutIntervalDTO> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(List<PayOutIntervalDTO> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public PayOutCTADistributionDTO getPayOutDistribution() {
        return payOutDistribution;
    }

    public void setPayOutDistribution(PayOutCTADistributionDTO payOutDistribution) {
        this.payOutDistribution = payOutDistribution;
    }
}
