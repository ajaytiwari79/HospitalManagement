package com.kairos.activity.response.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.phase.Phase;

import com.kairos.activity.persistence.model.activity.Activity;
import org.joda.time.Interval;

import java.math.BigInteger;
import java.util.Date;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftQueryResultWithActivity {

    private BigInteger id;
    private String name;

    private Date startDate;

    private Date endDate;

    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    private BigInteger activityId;
    private Long unitEmploymentPositionId;
    private Long staffId;
    private Phase phase;
    private Integer weekCount;
    private static boolean overrideWeekCount;
    private Long unitId;
    private Activity activity;
    private int scheduledMinutes;
    private int durationMinutes;
    private ShiftQueryResultWithActivity subShift;

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public ShiftQueryResultWithActivity() {
    }

    public ShiftQueryResultWithActivity(Date startDate, Date endDate, Activity activity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activity = activity;
    }

    public void setUnitEmploymentPositionId(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
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

    public ShiftQueryResultWithActivity getSubShift() {
        return subShift;
    }

    public void setSubShift(ShiftQueryResultWithActivity subShift) {
        this.subShift = subShift;
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

    public static boolean isOverrideWeekCount() {
        return overrideWeekCount;
    }

    public static void setOverrideWeekCount(boolean overrideWeekCount) {
        ShiftQueryResultWithActivity.overrideWeekCount = overrideWeekCount;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Interval getInterval(){
       return new Interval(startDate.getTime(),endDate.getTime());
    }

}
