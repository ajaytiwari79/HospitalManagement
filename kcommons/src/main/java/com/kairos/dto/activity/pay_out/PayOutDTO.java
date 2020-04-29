package com.kairos.dto.activity.pay_out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@NoArgsConstructor
public class PayOutDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalPayOutAfterCtaMin;
    private long totalPayOutBeforeCtaMin;
    private long payOutChange;
    private long protectedDaysOffMinutes;

    //Distributed min on the basis of Interval;
    private List<PayOutIntervalDTO> timeIntervals = new ArrayList<>();
    private PayOutCTADistributionDTO payOutDistribution;


    public PayOutDTO(Date startDate, Date endDate, long totalPayOutAfterCtaMin, long totalPayOutBeforeCtaMin,long payOutChange, List<PayOutIntervalDTO> timeIntervals, PayOutCTADistributionDTO payOutDistribution,long protectedDaysOffMinutes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
        this.payOutChange = payOutChange;
        this.timeIntervals = timeIntervals;
        this.payOutDistribution = payOutDistribution;
        this.protectedDaysOffMinutes = protectedDaysOffMinutes;
    }

}
