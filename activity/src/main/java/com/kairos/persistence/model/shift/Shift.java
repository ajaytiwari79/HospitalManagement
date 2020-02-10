package com.kairos.persistence.model.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.audit_logging.IgnoreLogging;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftActivityLineInterval;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.addMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.shift.ShiftType.SICK;

/**
 * Created by vipul on 30/8/17.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "shifts")
public class Shift extends MongoBaseEntity {

    protected Date startDate;
    protected Date endDate;
    protected boolean disabled = false;
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    protected Long staffId;
    protected BigInteger phaseId;
    protected BigInteger planningPeriodId;
    @Indexed
    protected Long unitId;
    protected int scheduledMinutes;
    protected int durationMinutes;
    @NotEmpty(message = "message.shift.activity.empty")
    protected List<ShiftActivity> activities;
    protected String externalId;
    protected String remarks;
    @NotNull(message = "error.ShiftDTO.employmentId.notnull")
    protected Long employmentId;
    protected BigInteger parentOpenShiftId;
    // from which shift it is copied , if we need to undo then we need this
    protected BigInteger copiedFromShiftId;
    protected boolean sickShift;
    protected Long functionId;
    protected Long staffUserId;
    protected ShiftType shiftType;
    protected int timeBankCtaBonusMinutes;
    protected int plannedMinutesOfTimebank;
    protected int payoutCtaBonusMinutes;
    protected int plannedMinutesOfPayout;
    protected int scheduledMinutesOfTimebank;
    protected int scheduledMinutesOfPayout;
    protected Shift draftShift;
    protected boolean draft;
    protected RequestAbsence requestAbsence;
    protected List<ShiftActivity> breakActivities;
    protected AccessGroupRole accessGroupRole;
    protected LocalDate validated;

    public Shift() {
        //Default Constructor
    }


    public Shift(Date startDate, Date endDate, Long employmentId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> shiftActivities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentId = employmentId;
        this.activities = shiftActivities;
    }

    // This is used in absance shift
    public Shift(Date startDate, Date endDate, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities, Long employmentId, Long unitId, BigInteger phaseId, BigInteger planningPeriodId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.activities = activities;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.phaseId = phaseId;
        this.planningPeriodId = planningPeriodId;

    }

    public Shift(Date startDate, Date endDate, String remarks, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, Long unitId, int scheduledMinutes, int durationMinutes, String externalId, Long employmentId, BigInteger parentOpenShiftId, BigInteger copiedFromShiftId, BigInteger phaseId, BigInteger planningPeriodId, Long staffUserId, ShiftType shiftType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
        this.externalId = externalId;
        this.employmentId = employmentId;
        this.parentOpenShiftId = parentOpenShiftId;
        this.copiedFromShiftId = copiedFromShiftId;
        this.scheduledMinutes = scheduledMinutes;
        this.durationMinutes = durationMinutes;
        this.phaseId = phaseId;
        this.planningPeriodId = planningPeriodId;
        this.staffUserId = staffUserId;
        this.shiftType = shiftType;
    }

    public void setBreakActivities(List<ShiftActivity> breakActivities) {
        this.breakActivities = isNullOrElse(breakActivities, new ArrayList<>());
    }

    public List<ShiftActivity> getBreakActivities() {
        return isNullOrElse(breakActivities, new ArrayList<>());
    }

    public void setActivities(List<ShiftActivity> activities) {
        activities = isNull(activities) ? new ArrayList<>() : activities;
        Collections.sort(activities);
        this.activities = activities;
    }

    public List<ShiftActivity> getActivities() {
        return isNullOrElse(activities,new ArrayList<>());
    }

    public int getMinutes() {
        DateTimeInterval interval = getInterval();
        return isNotNull(interval) ? (int) interval.getMinutes() : 0;
    }

    @IgnoreLogging
    public DateTimeInterval getInterval() {
        if (isCollectionNotEmpty(this.activities)) {
            return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), getActivities().get(getActivities().size() - 1).getEndDate().getTime());
        }
        return null;
    }

    public boolean isShiftUpdated(Shift shift) {
        if (this.getActivities().size() != shift.getActivities().size()) {
            return true;
        }
        for (int i = 0; i < shift.getActivities().size(); i++) {
            ShiftActivity thisShiftActivity = this.getActivities().get(i);
            ShiftActivity shiftActivity = shift.getActivities().get(i);
            if (thisShiftActivity.isShiftActivityChanged(shiftActivity)) {
                return true;
            }
        }
        return false;
    }

    public List<ShiftActivity>[] getShiftActivitiesForValidatingStaffingLevel(Shift shift) {
        List<ShiftActivity> shiftActivitiesForUnderStaffing = new ArrayList<>();
        List<ShiftActivity> shiftActivitiesForOverStaffing = new ArrayList<>();
        if (shift == null) {
            for (int i = 0; i < this.getActivities().size(); i++) {
                shiftActivitiesForOverStaffing.add(new ShiftActivity(this.getActivities().get(i).getActivityId(),this.getActivities().get(i).getStartDate(),this.getActivities().get(i).getEndDate(),this.getActivities().get(i).getActivityName()));
            }
        } else if (this == shift) {
            for (int i = 0; i < this.getActivities().size(); i++) {
                shiftActivitiesForUnderStaffing.add(new ShiftActivity(this.getActivities().get(i).getActivityId(),this.getActivities().get(i).getStartDate(),this.getActivities().get(i).getEndDate(),this.getActivities().get(i).getActivityName()));
            }
        } else {
            List<ShiftActivityLineInterval> shiftActivityLines=getShiftActivityLineIntervals(shift);
            List<ShiftActivityLineInterval> currentShiftActivityLines=getShiftActivityLineIntervals(this);
            shiftActivitiesForUnderStaffing= getActivitiesForValidatingStaffingLevel(currentShiftActivityLines,shiftActivityLines);
            shiftActivitiesForOverStaffing= getActivitiesForValidatingStaffingLevel(shiftActivityLines,currentShiftActivityLines);

        }

        return new List[] {shiftActivitiesForOverStaffing,shiftActivitiesForUnderStaffing};
    }

    protected List<ShiftActivityLineInterval> getShiftActivityLineIntervals(Shift shift){
        List<ShiftActivityLineInterval> shiftActivityLineIntervals=new ArrayList<>();
        for (ShiftActivity shiftActivity:shift.getActivities()) {
            Date endDateToBeSet=shiftActivity.getStartDate();
            Date startDateToBeSet=shiftActivity.getStartDate();
            while (endDateToBeSet.before(shiftActivity.getEndDate())){
                endDateToBeSet= addMinutes(endDateToBeSet,15);
                shiftActivityLineIntervals.add(new ShiftActivityLineInterval(startDateToBeSet,endDateToBeSet,shiftActivity.getActivityId(),shiftActivity.getActivityName()));
                startDateToBeSet=endDateToBeSet;
            }
        }
        return shiftActivityLineIntervals;
    }

    protected List<ShiftActivity> getActivitiesForValidatingStaffingLevel(List<ShiftActivityLineInterval> currentActivityLines, List<ShiftActivityLineInterval> shiftActivityLines){
        List<ShiftActivity> shiftActivitiesForCheckingStaffingLevel = new ArrayList<>();
        for (ShiftActivityLineInterval activityLineInterval:currentActivityLines){
            if(shiftActivityLines.stream().noneMatch(k->k.getStartDate().equals(activityLineInterval.getStartDate()) && k.getActivityId().equals(activityLineInterval.getActivityId()))){
                shiftActivitiesForCheckingStaffingLevel.add(new ShiftActivity(activityLineInterval.getActivityId(),activityLineInterval.getStartDate(),activityLineInterval.getEndDate(),activityLineInterval.getActivityName()));
            }
        }
        if(isCollectionNotEmpty(shiftActivitiesForCheckingStaffingLevel))
          mergeShiftActivityList(shiftActivitiesForCheckingStaffingLevel);
        return shiftActivitiesForCheckingStaffingLevel;
    }

    protected List<ShiftActivity> mergeShiftActivityList(List<ShiftActivity> shiftActivities){
        List<ShiftActivity> shiftActivitiesList=new ArrayList<>();
        ShiftActivity shiftActivity=shiftActivities.get(0);
        boolean activityAdded=false;
        for (int i = 0; i < shiftActivities.size()-2; i++) {
            if(activityAdded){
                shiftActivity=shiftActivities.get(i);
                activityAdded=false;
            }
            if(shiftActivities.get(i).getEndDate().equals(shiftActivities.get(i+1).getStartDate()) && shiftActivities.get(i).getActivityId().equals(shiftActivities.get(i+1).getActivityId())){
                shiftActivity.setEndDate(shiftActivities.get(i+1).getEndDate());
            }else {
                shiftActivitiesList.add(shiftActivity);
                activityAdded=true;
            }
        }
        return shiftActivitiesList;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public boolean isSickShift() {
        return SICK.equals(this.shiftType);
    }

    public Set<ShiftStatus> getShiftStatuses() {
        return getActivities().stream().flatMap(shiftActivity -> shiftActivity.getStatus().stream()).collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean isActivityMatch(BigInteger activityId,boolean includeDraftShift){
        boolean activityMatch;
        if(!includeDraftShift && this.draft){
            activityMatch = false;
        }else {
            activityMatch = this.getActivities().stream().anyMatch(shiftActivity -> shiftActivity.getActivityId().equals(activityId));
            if (!activityMatch && includeDraftShift) {
                activityMatch = isNotNull(this.getDraftShift()) ? this.getDraftShift().getActivities().stream().anyMatch(shiftActivity -> shiftActivity.getActivityId().equals(activityId)) : false;
            }
        }
        return activityMatch;
    }

    @Override
    public String toString() {
        return "Shift{" +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", disabled=" + disabled +
                ", remarks='" + remarks + '\'' +
                ", staffId=" + staffId +
                ", unitId=" + unitId +
                '}';
    }
}
