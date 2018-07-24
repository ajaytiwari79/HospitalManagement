package com.kairos.persistence.model.pay_out;

import com.kairos.enums.payout.PayOutStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class PayOut extends MongoBaseEntity{

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
    private PayOutStatus payOutStatus;
    private boolean paidout;
    private List<PayOutCTADistribution> payOutCTADistributions;


    public PayOut(BigInteger shiftId,Long unitPositionId, Long staffId, LocalDate date,Long unitId) {
        this.shiftId = shiftId;
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.date = date;
        this.payOutStatus = PayOutStatus.APPROVED;
        this.unitId = unitId;
    }

    public long getPayoutBeforeThisDate() {
        return payoutBeforeThisDate;
    }

    public void setPayoutBeforeThisDate(long payoutBeforeThisDate) {
        this.payoutBeforeThisDate = payoutBeforeThisDate;
    }

    public boolean isPaidout() {
        return paidout;
    }


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public void setPaidout(boolean paidout) {
        this.paidout = paidout;
    }

    public PayOutStatus getPayOutStatus() {
        return payOutStatus;
    }

    public void setPayOutStatus(PayOutStatus payOutStatus) {
        this.payOutStatus = payOutStatus;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public List<PayOutCTADistribution> getPayOutCTADistributions() {
        return payOutCTADistributions;
    }

    public void setPayOutCTADistributions(List<PayOutCTADistribution> payOutCTADistributions) {
        this.payOutCTADistributions = payOutCTADistributions;
    }


    public PayOut() {
    }

    public PayOut(Long unitPositionId, Long staffId, long totalPayOutMin, LocalDate date, PayOutStatus payOutStatus) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.totalPayOutMin = totalPayOutMin;
        this.date = date;
        this.payOutStatus = payOutStatus;
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
