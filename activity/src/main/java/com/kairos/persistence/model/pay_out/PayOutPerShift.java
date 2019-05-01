package com.kairos.persistence.model.pay_out;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class PayOutPerShift extends MongoBaseEntity{

    private Long unitPositionId;
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
    private List<PayOutPerShiftCTADistribution> payOutPerShiftCTADistributions;


    public PayOutPerShift(BigInteger shiftId, Long unitPositionId, Long staffId, LocalDate date, Long unitId) {
        this.shiftId = shiftId;
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.date = date;
        this.unitId = unitId;
    }

    public long getPayoutBeforeThisDate() {
        return payoutBeforeThisDate;
    }

    public void setPayoutBeforeThisDate(long payoutBeforeThisDate) {
        this.payoutBeforeThisDate = payoutBeforeThisDate;
    }

    public boolean isPaidOut() {
        return paidOut;
    }


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public void setPaidOut(boolean paidOut) {
        this.paidOut = paidOut;
    }



    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public List<PayOutPerShiftCTADistribution> getPayOutPerShiftCTADistributions() {
        return payOutPerShiftCTADistributions;
    }

    public void setPayOutPerShiftCTADistributions(List<PayOutPerShiftCTADistribution> payOutPerShiftCTADistributions) {
        this.payOutPerShiftCTADistributions = payOutPerShiftCTADistributions;
    }


    public PayOutPerShift() {
    }

    public PayOutPerShift(Long unitPositionId, Long staffId, long totalPayOutMinutes, LocalDate date) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.totalPayOutMinutes = totalPayOutMinutes;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(long scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public long getCtaBonusMinutesOfPayOut() {
        return ctaBonusMinutesOfPayOut;
    }

    public void setCtaBonusMinutesOfPayOut(long ctaBonusMinutesOfPayOut) {
        this.ctaBonusMinutesOfPayOut = ctaBonusMinutesOfPayOut;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public long getTotalPayOutMinutes() {
        return totalPayOutMinutes;
    }

    public void setTotalPayOutMinutes(long totalPayOutMinutes) {
        this.totalPayOutMinutes = totalPayOutMinutes;
    }



}
