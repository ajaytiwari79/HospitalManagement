package com.kairos.persistence.model.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.commons.utils.DateTimeInterval;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 30/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "shifts")
public class Shift extends MongoBaseEntity {

    private Date startDate;
    private Date endDate;
    private boolean disabled = false;
    private long bid;
    private long pId;
    private long bonusTimeBank = 0;
    private long amount;
    private long probability = 0;
    private long accumulatedTimeBankInMinutes = 0;
    private String remarks;
    private Long staffId;
    private BigInteger phaseId;
    private BigInteger planningPeriodId;
    private Integer weekCount;
    @Indexed
    private Long unitId;
    private int scheduledMinutes;
    private int durationMinutes;
    private List<ShiftActivity> activities;
    //time care id
    private String externalId;
    private Long unitPositionId;
    private BigInteger parentOpenShiftId;
    // from which shift it is copied , if we need to undo then we need this
    private BigInteger copiedFromShiftId;
    private boolean sickShift;
    private Long createdBy ;//= UserContext.getUserDetails().getId();
    private Long updatedBy ;//= UserContext.getUserDetails().getId();
    private Long functionId;
    private Long staffUserId;
    private ShiftType shiftType;

    public Long getStaffUserId() {
        return staffUserId;
    }

    public void setStaffUserId(Long staffUserId) {
        this.staffUserId = staffUserId;
    }

    public Shift() {
        //Default Constructor
    }


    public Shift(Date startDate, Date endDate, Long unitPositionId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitPositionId = unitPositionId;
    }

    public Shift(BigInteger id, Date startDate, Date endDate, long bid, long pId, long bonusTimeBank,
                 long amount, long probability, long accumulatedTimeBankInMinutes, String remarks,List<ShiftActivity> activities, Long staffId, Long unitId, Long unitPositionId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = bid;
        this.pId = pId;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
        this.unitPositionId = unitPositionId;

    }
    // This is used in absance shift
    public Shift(Date startDate, Date endDate, Long staffId,List<ShiftActivity> activities,Long unitPositionId,Long unitId,BigInteger phaseId,BigInteger planningPeriodId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.activities = activities;
        this.unitPositionId=unitPositionId;
        this.unitId=unitId;
        this.sickShift=true;
        this.phaseId=phaseId;
        this.planningPeriodId=planningPeriodId;

    }

    public Shift( Date startDate, Date endDate, String remarks, List<ShiftActivity> activities, Long staffId,Long unitId, int scheduledMinutes, int durationMinutes, String externalId, Long unitPositionId,  BigInteger parentOpenShiftId, BigInteger copiedFromShiftId,BigInteger phaseId,BigInteger planningPeriodId,Long staffUserId,ShiftType shiftType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
        this.externalId = externalId;
        this.unitPositionId = unitPositionId;
        this.parentOpenShiftId = parentOpenShiftId;
        this.copiedFromShiftId = copiedFromShiftId;
        this.scheduledMinutes = scheduledMinutes;
        this.durationMinutes = durationMinutes;
        this.phaseId=phaseId;
        this.planningPeriodId=planningPeriodId;
        this.staffUserId=staffUserId;
        this.shiftType=shiftType;
    }


    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(int scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public List<ShiftActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivity> activities) {
        activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
        this.activities = activities;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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

    public int getMinutes() {
        return (int)getInterval().getMinutes();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getStaffId() {
        return staffId;
    }


    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }


    public Integer getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Integer weekCount) {
        this.weekCount = weekCount;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }


    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;

    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }

    public BigInteger getCopiedFromShiftId() {
        return copiedFromShiftId;
    }

    public void setCopiedFromShiftId(BigInteger copiedFromShiftId) {
        this.copiedFromShiftId = copiedFromShiftId;
    }

    public boolean isSickShift() {
        return sickShift;
    }

    public void setSickShift(boolean sickShift) {
        this.sickShift = sickShift;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public BigInteger getPlanningPeriodId() {
        return planningPeriodId;
    }

    public void setPlanningPeriodId(BigInteger planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), getActivities().get(getActivities().size()-1).getEndDate().getTime());
    }



    @Override
    public String toString() {
        return "Shift{" +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", disabled=" + disabled +
                ", bid=" + bid +
                ", pId=" + pId +
                ", bonusTimeBank=" + bonusTimeBank +
                ", amount=" + amount +
                ", probability=" + probability +
                ", accumulatedTimeBankInMinutes=" + accumulatedTimeBankInMinutes +
                ", remarks='" + remarks + '\'' +
                ", staffId=" + staffId +
                ", weekCount=" + weekCount +
                ", unitId=" + unitId +
                '}';
    }

}
