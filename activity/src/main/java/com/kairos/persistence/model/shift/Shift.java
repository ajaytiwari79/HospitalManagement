package com.kairos.persistence.model.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.dto.activity.shift.ShiftQueryResult;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.shift.ShiftStatus;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

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
    private Phase phase;// todo REMOVE VIPUL
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
    private Set<ShiftStatus> status = new HashSet<>(Arrays.asList(ShiftStatus.UNPUBLISHED));

    private BigInteger parentOpenShiftId;
    private Long allowedBreakDurationInMinute;

    // from which shift it is copied , if we need to undo then we need this
    private BigInteger copiedFromShiftId;
    private BigInteger plannedTimeId; // This is calculated by Phase and unit settings.

    private boolean sickShift;
    private LocalDate validatedByStaffDate;
    private LocalDate validatedByPlannerDate;

    public Shift() {
        //Default Constructor
    }


    public Shift(Date startDate, Date endDate, Long unitPositionId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitPositionId = unitPositionId;
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

    public Shift(Date startDate, Date endDate, Long staffId, List<ShiftActivity> activities, Long unitPositionId, Long unitId,BigInteger phaseId,BigInteger planningPeriodId) {
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

    public List<ShiftActivity> getActivities() {
        return activities;
    }

    public List<ShiftActivity> getSortedActvities(){
        activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
        return activities;
    }


    public void setActivities(List<ShiftActivity> activities) {
        this.activities = activities;
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


    public LocalDate getValidatedByStaffDate() {
        return validatedByStaffDate;
    }

    public void setValidatedByStaffDate(LocalDate validatedByStaffDate) {
        this.validatedByStaffDate = validatedByStaffDate;
    }

    public LocalDate getValidatedByPlannerDate() {
        return validatedByPlannerDate;
    }

    public void setValidatedByPlannerDate(LocalDate validatedByPlannerDate) {
        this.validatedByPlannerDate = validatedByPlannerDate;
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


    public DateTimeInterval getDateTimeInterval() {
        return new DateTimeInterval(this.startDate.getTime(), this.getEndDate().getTime());
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
                ", phase=" + phase +
                ", weekCount=" + weekCount +
                ", unitId=" + unitId +
                ", status=" + status +
                '}';
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
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



    public ShiftQueryResult getShiftQueryResult() {
        ShiftQueryResult shiftQueryResult = new ShiftQueryResult(this.id,
                this.startDate,
                this.endDate,
                this.bid,
                this.pId,
                this.bonusTimeBank,
                this.amount,
                this.probability,
                this.accumulatedTimeBankInMinutes,
                this.remarks,
                this.activities, this.staffId, this.unitId, this.unitPositionId);
        shiftQueryResult.setStatus(this.getStatus());
        shiftQueryResult.setAllowedBreakDurationInMinute(this.allowedBreakDurationInMinute);
        shiftQueryResult.setPlannedTimeId(this.plannedTimeId);
        return shiftQueryResult;
    }

    public ShiftDTO getShiftDTO() {
        ShiftDTO shiftDTO = new ShiftDTO(this.id,
                this.startDate,
                this.endDate,
                this.bid,
                this.pId,
                this.bonusTimeBank,
                this.amount,
                this.probability,
                this.accumulatedTimeBankInMinutes,
                this.remarks,
                this.activities, this.staffId, this.unitId, this.unitPositionId);
        shiftDTO.setStatus(this.getStatus());
        shiftDTO.setAllowedBreakDurationInMinute(this.allowedBreakDurationInMinute);
        shiftDTO.setPlannedTimeId(this.plannedTimeId);
        return shiftDTO;
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

    public Set<ShiftStatus> getStatus() {
        return status=Optional.ofNullable(status).orElse(new HashSet<>());
    }

    public void setStatus(Set<ShiftStatus> status) {
        this.status = status;
    }

    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public BigInteger getCopiedFromShiftId() {
        return copiedFromShiftId;
    }

    public void setCopiedFromShiftId(BigInteger copiedFromShiftId) {
        this.copiedFromShiftId = copiedFromShiftId;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
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

    public Shift( Date startDate, Date endDate, String remarks, List<ShiftActivity> activities, Long staffId, Phase phase, Long unitId, int scheduledMinutes, int durationMinutes, String externalId, Long unitPositionId, Set<ShiftStatus> status, BigInteger parentOpenShiftId, Long allowedBreakDurationInMinute, BigInteger copiedFromShiftId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.phase = phase;
        this.unitId = unitId;
        this.externalId = externalId;
        this.unitPositionId = unitPositionId;
        this.status = status;
        this.parentOpenShiftId = parentOpenShiftId;
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
        this.copiedFromShiftId = copiedFromShiftId;
        this.scheduledMinutes = scheduledMinutes;
        this.durationMinutes = durationMinutes;
    }

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.startDate.getTime(), this.endDate.getTime());
    }

}
