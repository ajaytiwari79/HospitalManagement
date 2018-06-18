package com.kairos.activity.persistence.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalTime;

public class ShiftTemplate extends MongoBaseEntity {
    private String name;
    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.activityId.notnull")
    private BigInteger activityId;
    private Long unitId;
    @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.staffId.notnull")
    private Long staffId;
    @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.unitPositionId.notnull")
    private Long unitPositionId;
    private int scheduledMinutes;
    private int durationMinutes;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private Long allowedBreakDurationInMinute;

    public ShiftTemplate() {
        //Default Constructor
    }

    public ShiftTemplate(String name, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes,
                         String remarks, @Range(min = 0) @NotNull(message = "error.shiftTemplate.activityId.notnull") BigInteger activityId, Long unitId, @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.staffId.notnull") Long staffId, @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.unitPositionId.notnull") Long unitPositionId, int scheduledMinutes,
                         int durationMinutes, LocalTime startTime, LocalTime endTime, Long allowedBreakDurationInMinute) {
        this.name = name;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activityId = activityId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.unitPositionId = unitPositionId;
        this.scheduledMinutes = scheduledMinutes;
        this.durationMinutes = durationMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public int getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(int scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }
}
