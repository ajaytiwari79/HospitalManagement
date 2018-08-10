package com.kairos.activity.pay_out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.payout.PayOutTrasactionStatus;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayOutIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalPayOutAfterCtaMin;
    private long totalPayOutBeforeCtaMin;
    private long payoutChange;
    private String title;
    private PayOutCTADistributionDTO payOutDistribution;
    private DayOfWeek dayOfWeek;




    public PayOutIntervalDTO(Date startDate, Date endDate, long totalPayOutAfterCtaMin, long totalPayOutBeforeCtaMin, long payoutChange, PayOutCTADistributionDTO payOutDistribution,DayOfWeek dayOfWeek,String title) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
        this.payOutDistribution = payOutDistribution;
        this.payoutChange = payoutChange;
        this.dayOfWeek = dayOfWeek;
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PayOutIntervalDTO() {
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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


    public long getPayoutChange() {
        return payoutChange;
    }

    public void setPayoutChange(long payoutChange) {
        this.payoutChange = payoutChange;
    }

    public PayOutCTADistributionDTO getPayOutDistribution() {
        return payOutDistribution;
    }

    public void setPayOutDistribution(PayOutCTADistributionDTO payOutDistribution) {
        this.payOutDistribution = payOutDistribution;
    }

}
