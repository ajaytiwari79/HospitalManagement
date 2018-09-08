package com.kairos.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.shift.ShiftStatus;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.*;

/**
 * Created by vipul on 31/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftQueryResult {
    private BigInteger id;
    private String name;

    private Date startDate;

    private Date endDate;

    private long bid;
    private long pId;
    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    private BigInteger activityId;
    private Long staffId;
    private Integer weekCount;
    private static boolean overrideWeekCount;
    private Long unitId;
    private List<ShiftQueryResult> subShifts = new ArrayList<>();
    private Long unitPositionId;
    private int scheduledMinutes;
    private int durationMinutes;
    private Set<ShiftStatus> status;
    private Long allowedBreakDurationInMinute;
    private Long expertiseId;
    private String timeType;
    private BigInteger plannedTimeId; // This is calculated by Phase and unit settings.
    public ShiftQueryResult() {
        //DC
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }



    public Integer getWeekCount() {
        if (!overrideWeekCount) {
            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            weekCount = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
        return weekCount;
    }

    public Integer convertCustomWeekCount(Instant date) {
        ZonedDateTime now = ZonedDateTime.ofInstant(date, ZoneId.systemDefault());
        Integer convertedWeekCount = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return convertedWeekCount;
    }

    public void setWeekCount(Integer weekCount) {
        this.weekCount = weekCount;
    }

    public static Boolean getOverrideWeekCount() {
        return overrideWeekCount;
    }

    public static void setOverrideWeekCount(Boolean overrideWeekCount) {
        ShiftQueryResult.overrideWeekCount = overrideWeekCount;
    }

    public static boolean isOverrideWeekCount() {
        return overrideWeekCount;
    }

    public static void setOverrideWeekCount(boolean overrideWeekCount) {
        ShiftQueryResult.overrideWeekCount = overrideWeekCount;
    }


    public Long getStartDate() {
        return startDate.getTime();
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate.getTime();
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }


    public List<ShiftQueryResult> getSubShifts() {
        return subShifts;
    }

    public void setSubShifts(List<ShiftQueryResult> subShifts) {
        this.subShifts = subShifts;
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

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public ShiftQueryResult(BigInteger id, String name, Date startDate, Date endDate, long bid, long pId, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, BigInteger activityId, Long staffId, Long unitId, Long unitPositionId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = bid;
        this.pId = pId;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activityId = activityId;
        this.staffId = staffId;
        this.unitId = unitId;
        this.unitPositionId = unitPositionId;
    }


    public List<ShiftQueryResult> sortShifts() {
        if (Optional.ofNullable(subShifts).isPresent()) {
            subShifts.sort((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
        }
        return subShifts;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }


    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;

    }
}

