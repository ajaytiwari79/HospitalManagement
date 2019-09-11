package com.kairos.persistence.model.activity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Vipul on 18/1/18.
 */

public class SubShifts {

    private Date startDate;
    private Date endDate;
    @Indexed
    private BigInteger activityId;
    private BigInteger parentShiftId;
    private long bid;
    private long pId;
    private long bonusTimeBank = 0;
    private long amount;
    private long probability = 0;
    private long accumulatedTimeBankInMinutes = 0;
    private String remarks;


    public SubShifts() {
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

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public SubShifts(Date startDate, Date endDate, BigInteger activityId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityId = activityId;
    }

    public BigInteger getParentShiftId() {
        return parentShiftId;
    }

    public void setParentShiftId(BigInteger parentShiftId) {
        this.parentShiftId = parentShiftId;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public long getBonusTimeBank() {
        return bonusTimeBank;
    }

    public void setBonusTimeBank(long bonusTimeBank) {
        this.bonusTimeBank = bonusTimeBank;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getProbability() {
        return probability;
    }

    public void setProbability(long probability) {
        this.probability = probability;
    }

    public long getAccumulatedTimeBankInMinutes() {
        return accumulatedTimeBankInMinutes;
    }

    public void setAccumulatedTimeBankInMinutes(long accumulatedTimeBankInMinutes) {
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SubShifts subShifts = (SubShifts) o;

        return new EqualsBuilder()
                .append(startDate, subShifts.startDate)
                .append(endDate, subShifts.endDate)
                .append(activityId, subShifts.activityId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(startDate)
                .append(endDate)
                .append(activityId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("activityId", activityId)
                .toString();
    }
}
