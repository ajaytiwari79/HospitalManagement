package com.kairos.wrapper.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.phase.Phase;
import org.joda.time.Interval;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftWithActivityDTO {

    private BigInteger id;
    private String name;
    private List<ShiftActivityDTO> activities = new ArrayList<>();
    private Date startDate;

    private Date endDate;

    private long bonusTimeBank;
    private long amount;
    private long probability;
    private long accumulatedTimeBankInMinutes;
    private String remarks;
    private Long unitPositionId;
    private BigInteger planningPeriodId;
    private Long staffId;
    private Phase phase;
    private Integer weekCount;
    private LocalDate shiftDate;
    private static boolean overrideWeekCount;
    private Long unitId;
    private int scheduledMinutes;
    private int durationMinutes;

    private List<ShiftStatus> status;
    private String timeType;
    private List<BigInteger> activitiesTimeTypeIds = new ArrayList<>();
    private List<BigInteger> activityIds = new ArrayList<>();
    private List<BigInteger> activitiesPlannedTimeIds = new ArrayList<>();
    private BigInteger phaseId;
    private ShiftType shiftType;

    //~ ===================================Constructors=======================================================
    public ShiftWithActivityDTO() {
    }

    public ShiftWithActivityDTO(BigInteger id, String name, Date startDate, Date endDate, long bonusTimeBank, long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, List<ShiftActivityDTO> activities, Long staffId, Long unitPositionId, Long unitId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activities = activities;
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.unitId = unitId;
    }

    public ShiftWithActivityDTO(Date startDate, Date endDate, Activity activity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activities = activities;
    }


    public List<BigInteger> getActivitiesTimeTypeIds(){
        if(activitiesTimeTypeIds.isEmpty()) {
            activitiesTimeTypeIds = activities.stream().map(shiftActivityDTO -> shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).collect(Collectors.toList());
        }
        return activitiesTimeTypeIds;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public List<BigInteger> getActivitiesPlannedTimeIds(){
        if(activitiesPlannedTimeIds.isEmpty()) {
            activitiesPlannedTimeIds = activities.stream().map(shiftActivityDTO -> shiftActivityDTO.getPlannedTimeId()).collect(Collectors.toList());
        }
        return activitiesPlannedTimeIds;
    }


    public List<BigInteger> getActivitIds(){
        if(activityIds.isEmpty()) {
            activityIds = activities.stream().map(shiftActivityDTO -> shiftActivityDTO.getActivityId()).collect(Collectors.toList());
        }
        return activityIds;
    }

    public BigInteger getPlanningPeriodId() {
        return planningPeriodId;
    }

    public void setPlanningPeriodId(BigInteger planningPeriodId) {
        this.planningPeriodId = planningPeriodId;
    }
    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    //~ ===================================Setters and Getters=======================================================
    public List<ShiftStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ShiftStatus> status) {
        this.status = status;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public List<ShiftActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
        this.activities = activities;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public int getMinutes() {
        return ((int) (this.activities.get(0).getEndDate().getTime() - this.activities.get(activities.size() - 1).getStartDate().getTime()) / 60000);
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


    public DateTimeInterval getDateTimeInterval() {
        return new DateTimeInterval(startDate.getTime(), endDate.getTime());
    }

    public Interval getInterval() {
        return new Interval(startDate.getTime(), endDate.getTime());
    }


    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
}
