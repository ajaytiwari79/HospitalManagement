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
    private long totalPayOutMin;
    private long contractualMin;
    private long scheduledMin;
    private long payOutMinWithoutCta;
    private long payOutMinWithCta;
    private long payoutBeforeThisDate;
    private LocalDate date;
    private boolean paidOut;
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

    public PayOutPerShift(Long unitPositionId, Long staffId, long totalPayOutMin, LocalDate date) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.totalPayOutMin = totalPayOutMin;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getScheduledMin() {
        return scheduledMin;
    }

    public void setScheduledMin(long scheduledMin) {
        this.scheduledMin = scheduledMin;
    }

    public long getPayOutMinWithoutCta() {
        return payOutMinWithoutCta;
    }

    public void setPayOutMinWithoutCta(long payOutMinWithoutCta) {
        this.payOutMinWithoutCta = payOutMinWithoutCta;
    }

    public long getPayOutMinWithCta() {
        return payOutMinWithCta;
    }

    public void setPayOutMinWithCta(long payOutMinWithCta) {
        this.payOutMinWithCta = payOutMinWithCta;
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

    public long getTotalPayOutMin() {
        return totalPayOutMin;
    }

    public void setTotalPayOutMin(long totalPayOutMin) {
        this.totalPayOutMin = totalPayOutMin;
    }

    public long getContractualMin() {
        return contractualMin;
    }

    public void setContractualMin(long contractualMin) {
        this.contractualMin = contractualMin;
    }


}
