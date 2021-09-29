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
    private double totalPayOutAfterCtaMin;
    private Double totalPayOutBeforeCtaMin;
    private double payoutChange;
    private double payOutChangeCost;
    private String title;
    private PayOutCTADistributionDTO payOutDistribution;
    private DayOfWeek dayOfWeek;
    private double protectedDaysOffMinutes;
    private int sequence;




    public PayOutIntervalDTO(Date startDate, Date endDate, double totalPayOutAfterCtaMin, double totalPayOutBeforeCtaMin, double payoutChange, PayOutCTADistributionDTO payOutDistribution,DayOfWeek dayOfWeek,String title,double payOutChangeCost) {
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
