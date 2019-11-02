package com.kairos.persistence.model.pay_out;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Getter
@Setter
@Document
@NoArgsConstructor
public class PayOutPerShift extends MongoBaseEntity{

    private Long employmentId;
    private Long staffId;
    private BigInteger shiftId;
    private Long unitId;
    //In minutes
    private long totalPayOutMinutes;
    //It is the scheduled minutes of Ruletemplate which accountType is equal to PAID_OUT
    private long scheduledMinutes;
    //It Includes CTAcompensation of Function and Bonus Ruletemplate which accountType is equal to PAID_OUT
    private long ctaBonusMinutesOfPayOut;
    private long payoutBeforeThisDate;
    private LocalDate date;
    private boolean paidOut;
    //It Includes CTAcompensation of Function and Bonus Ruletemplate which accountType is equal to PAID_OUT
    private List<PayOutPerShiftCTADistribution> payOutPerShiftCTADistributions = new ArrayList<>();
    private long protectedDaysOffMinutes;
    private transient float cost;


    public PayOutPerShift(BigInteger shiftId, Long employmentId, Long staffId, LocalDate date, Long unitId) {
        this.shiftId = shiftId;
        this.employmentId = employmentId;
        this.staffId = staffId;
        this.date = date;
        this.unitId = unitId;
    }




    public PayOutPerShift(Long employmentId, Long staffId, long totalPayOutMinutes, LocalDate date) {
        this.employmentId = employmentId;
        this.staffId = staffId;
        this.totalPayOutMinutes = totalPayOutMinutes;
        this.date = date;
    }


}
