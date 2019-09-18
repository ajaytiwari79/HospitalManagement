package com.kairos.persistence.model.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.audit_logging.IgnoreLogging;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftActivityLineInterval;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.addMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;

/**
 * Created by vipul on 30/8/17.
 */
@Getter
@Setter
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
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    private Long staffId;
    private BigInteger phaseId;
    private BigInteger planningPeriodId;
    private Integer weekCount;
    @Indexed

    private Long unitId;
    private int scheduledMinutes;
    private int durationMinutes;
    @NotEmpty(message = "message.shift.activity.empty")
    private List<ShiftActivity> activities;
    //time care id
    private String externalId;
    @NotNull(message = "error.ShiftDTO.employmentId.notnull")
    private Long employmentId;
    private BigInteger parentOpenShiftId;
    // from which shift it is copied , if we need to undo then we need this
    private BigInteger copiedFromShiftId;
    private boolean sickShift;
    private Long functionId;
    private Long staffUserId;
    private ShiftType shiftType;
    private int timeBankCtaBonusMinutes;
    private int plannedMinutesOfTimebank;
    private int payoutCtaBonusMinutes;
    private int plannedMinutesOfPayout;
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private Shift draftShift;
    private boolean draft;
    private RequestAbsence requestAbsence;
    private List<ShiftActivity> breakActivities;


    public Shift() {
        //Default Constructor
    }


    public Shift(Date startDate, Date endDate, Long employmentId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> shiftActivities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentId = employmentId;
        this.activities = shiftActivities;
    }

    public Shift(BigInteger id, Date startDate, Date endDate, long bid, long pId, long bonusTimeBank,
                 long amount, long probability, long accumulatedTimeBankInMinutes, String remarks, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, Long unitId, Long employmentId) {
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
        this.employmentId = employmentId;

    }

    // This is used in absance shift
    public Shift(Date startDate, Date endDate, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities, Long employmentId, Long unitId, BigInteger phaseId, BigInteger planningPeriodId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.activities = activities;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.sickShift = true;
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
                shiftActivitiesForOverStaffing.add(new ShiftActivity(this.getActivities().get(i).getActivityId(),this.getActivities().get(i).getStartDate(),this.getActivities().get(i).getEndDate()));
            }
        } else if (this == shift) {
            for (int i = 0; i < this.getActivities().size(); i++) {
                shiftActivitiesForUnderStaffing.add(new ShiftActivity(this.getActivities().get(i).getActivityId(),this.getActivities().get(i).getStartDate(),this.getActivities().get(i).getEndDate()));
            }
        } else {
            List<ShiftActivityLineInterval> shiftActivityLines=getShiftActivityLineIntervals(shift);
            List<ShiftActivityLineInterval> currentShiftActivityLines=getShiftActivityLineIntervals(this);
            shiftActivitiesForUnderStaffing= getActivitiesForValidatingStaffingLevel(currentShiftActivityLines,shiftActivityLines);
            shiftActivitiesForOverStaffing= getActivitiesForValidatingStaffingLevel(shiftActivityLines,currentShiftActivityLines);

        }

        return new List[] {shiftActivitiesForOverStaffing,shiftActivitiesForUnderStaffing};
    }

    private List<ShiftActivityLineInterval> getShiftActivityLineIntervals(Shift shift){
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

    private List<ShiftActivity> getActivitiesForValidatingStaffingLevel(List<ShiftActivityLineInterval> currentActivityLines, List<ShiftActivityLineInterval> shiftActivityLines){
        List<ShiftActivity> shiftActivitiesForCheckingStaffingLevel = new ArrayList<>();
        for (ShiftActivityLineInterval activityLineInterval:currentActivityLines){
            if(shiftActivityLines.stream().noneMatch(k->k.getStartDate().equals(activityLineInterval.getStartDate()) && k.getActivityId().equals(activityLineInterval.getActivityId()))){
                shiftActivitiesForCheckingStaffingLevel.add(new ShiftActivity(activityLineInterval.getActivityId(),activityLineInterval.getStartDate(),activityLineInterval.getEndDate()));
            }
        }
        if(isCollectionNotEmpty(shiftActivitiesForCheckingStaffingLevel))
          mergeShiftActivityList(shiftActivitiesForCheckingStaffingLevel);
        return shiftActivitiesForCheckingStaffingLevel;
    }

    private List<ShiftActivity> mergeShiftActivityList(List<ShiftActivity> shiftActivities){
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
