package com.kairos.wrapper.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.util.DateTimeInterval;
import org.joda.time.Interval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftWithActivityDTO {

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
    private Long unitPositionId;
    private Long staffId;
    private Phase phase;
    private Integer weekCount;
    private static boolean overrideWeekCount;
    private Long unitId;
    private Activity activity;
    private int scheduledMinutes;
    private int durationMinutes;
    private ShiftWithActivityDTO subShift;
    private List<ShiftStatus> status;
    private List<BigInteger> brokenRuleTemplateIds = new ArrayList<>();
    private BigInteger plannedTypeId ;
    private String timeType;

    public List<ShiftStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ShiftStatus> status) {
        this.status = status;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public ShiftWithActivityDTO() {
    }


    public List<BigInteger> getBrokenRuleTemplateIds() {
        return brokenRuleTemplateIds;
    }

    public void setBrokenRuleTemplateIds(List<BigInteger> brokenRuleTemplateIds) {
        this.brokenRuleTemplateIds = brokenRuleTemplateIds;
    }


    public ShiftWithActivityDTO(BigInteger id, String name, Date startDate, Date endDate, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, BigInteger activityId, Long staffId, Long unitPositionId, Long unitId, Activity activity) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activityId = activityId;
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.unitId = unitId;
        this.activity = activity;
    }

    public ShiftWithActivityDTO(Date startDate, Date endDate, Activity activity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activity = activity;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public int getMinutes(){
        return ((int)(this.endDate.getTime() - this.startDate.getTime())/60000);
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

    public ShiftWithActivityDTO getSubShift() {
        return subShift;
    }

    public void setSubShift(ShiftWithActivityDTO subShift) {
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
        ShiftWithActivityDTO.overrideWeekCount = overrideWeekCount;
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

    public DateTimeInterval getDateTimeInterval(){
       return new DateTimeInterval(startDate.getTime(),endDate.getTime());
    }

    public Interval getInterval(){
        return new Interval(startDate.getTime(),endDate.getTime());
    }

    public BigInteger getPlannedTypeId() {
        return plannedTypeId;
    }

    public void setPlannedTypeId(BigInteger plannedTypeId) {
        this.plannedTypeId = plannedTypeId;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
}
