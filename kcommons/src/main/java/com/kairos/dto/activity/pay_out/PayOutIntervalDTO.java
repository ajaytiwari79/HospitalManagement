package com.kairos.dto.activity.pay_out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
public class PayOutIntervalDTO {

    private Date startDate;
    private Date endDate;
    //In minutes
    private long totalPayOutAfterCtaMin;
    private long totalPayOutBeforeCtaMin;
    private long payoutChange;
    private float payOutChangeCost;
    private String title;
    private PayOutCTADistributionDTO payOutDistribution;
    private DayOfWeek dayOfWeek;
    private long protectedDaysOffMinutes;
    private int sequence;




    public PayOutIntervalDTO(Date startDate, Date endDate, long totalPayOutAfterCtaMin, long totalPayOutBeforeCtaMin, long payoutChange, PayOutCTADistributionDTO payOutDistribution,DayOfWeek dayOfWeek,String title,float payOutChangeCost) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPayOutAfterCtaMin = totalPayOutAfterCtaMin;
        this.totalPayOutBeforeCtaMin = totalPayOutBeforeCtaMin;
        this.payOutDistribution = payOutDistribution;
        this.payoutChange = payoutChange;
        this.dayOfWeek = dayOfWeek;
        this.title = title;
        this.payOutChangeCost = payOutChangeCost;
    }

}
