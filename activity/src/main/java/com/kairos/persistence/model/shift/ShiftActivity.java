package com.kairos.persistence.model.shift;

import com.kairos.commons.audit_logging.IgnoreLogging;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 10/9/18
 */
@Getter
@Setter
public class ShiftActivity implements Comparable<ShiftActivity>{


    private BigInteger activityId;
    private Date startDate;
    private Date endDate;
    private int scheduledMinutes;
    private int durationMinutes;
    private String activityName;
    private long bid;
    private long pId;
    //used in T&A view
    private Long reasonCodeId;
    //used for adding absence type of activities.
    private Long absenceReasonCodeId;
    private String remarks;
    //please don't use this id for any functionality this on ly for frontend
    private BigInteger id;
    private String timeType;
    private String backgroundColor;
    private boolean haltBreak;
    private boolean breakShift;
    private boolean breakReplaced;
    private List<TimeBankCTADistribution> timeBankCTADistributions;
    private List<PayOutPerShiftCTADistribution> payoutPerShiftCTADistributions;
    private int payoutCtaBonusMinutes;
    private Long allowedBreakDurationInMinute;
    private int timeBankCtaBonusMinutes;
    private String startLocation; // this is for the location from where activity will gets starts
    private String endLocation;   // this is for the location from where activity will gets ends
    private int plannedMinutesOfTimebank;
    private int plannedMinutesOfPayout;
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private List<PlannedTime> plannedTimes;
    private List<ShiftActivity> childActivities;

    @IgnoreLogging
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getStartDate().getTime(), this.getEndDate().getTime());
    }

    private Set<ShiftStatus> status = new HashSet<>();

    public ShiftActivity() {
    }



    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId,String timeType) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.timeType = timeType;
    }

    public ShiftActivity(String activityName, Date startDate, Date endDate, BigInteger activityId, boolean breakShift, Long absenceReasonCodeId, Long allowedBreakDurationInMinute, String startLocation, String endLocation) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.breakShift=breakShift;
        this.absenceReasonCodeId = absenceReasonCodeId;
        this.allowedBreakDurationInMinute=allowedBreakDurationInMinute;
        this.startLocation=startLocation;
        this.endLocation=endLocation;
    }

    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId,boolean breakShift,Long absenceReasonCodeId,
                          Long allowedBreakDurationInMinute,String remarks,String startLocation,String endLocation) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.breakShift=breakShift;
        this.absenceReasonCodeId = absenceReasonCodeId;
        this.allowedBreakDurationInMinute=allowedBreakDurationInMinute;
        this.remarks = remarks;
        this.startLocation=startLocation;
        this.endLocation=endLocation;
    }
    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId,boolean breakShift,Long absenceReasonCodeId,
                          Long allowedBreakDurationInMinute,boolean breakReplaced,String startLocation,String endLocation) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.breakShift=breakShift;
        this.absenceReasonCodeId = absenceReasonCodeId;
        this.allowedBreakDurationInMinute=allowedBreakDurationInMinute;
        this.breakReplaced=breakReplaced;
        this.startLocation=startLocation;
        this.endLocation=endLocation;
    }
    public ShiftActivity(BigInteger activityId, String activityName) {
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public void setPayoutPerShiftCTADistributions(List<PayOutPerShiftCTADistribution> payoutPerShiftCTADistributions) {
        this.payoutPerShiftCTADistributions = isNullOrElse(payoutPerShiftCTADistributions,new ArrayList<>());
    }

    public List<PlannedTime> getPlannedTimes() {
        return plannedTimes=Optional.ofNullable(plannedTimes).orElse(new ArrayList<>());
    }

    public void getChildActivities(List<ShiftActivity> childActivities) {
        this.childActivities = isNullOrElse(childActivities,new ArrayList<>());
    }

    @Override
    public int compareTo(ShiftActivity shiftActivity) {
        return this.startDate.compareTo(shiftActivity.startDate);
    }
}
