package com.kairos.persistence.model.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 10/9/18
 */
public class ShiftActivity {


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
    private BigInteger plannedTimeId;
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

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getStartDate().getTime(), this.getEndDate().getTime());
    }

    private Set<ShiftStatus> status = new HashSet<>(Arrays.asList(ShiftStatus.REQUEST));

    public ShiftActivity() {
    }



    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
    }

    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId,boolean breakShift,Long absenceReasonCodeId,Long allowedBreakDurationInMinute,String startLocation,String endLocation) {
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


    public int getTimeBankCtaBonusMinutes() {
        return timeBankCtaBonusMinutes;
    }

    public void setTimeBankCtaBonusMinutes(int timeBankCtaBonusMinutes) {
        this.timeBankCtaBonusMinutes = timeBankCtaBonusMinutes;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }


    public Set<ShiftStatus> getStatus() {
        return status;
    }

    public void setStatus(Set<ShiftStatus> status) {
        this.status = status;
    }
    public boolean isHaltBreak() {
        return haltBreak;
    }

    public void setHaltBreak(boolean haltBreak) {
        this.haltBreak = haltBreak;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isBreakShift() {
        return breakShift;
    }

    public void setBreakShift(boolean breakShift) {
        this.breakShift = breakShift;
    }

    public Long getAbsenceReasonCodeId() {
        return absenceReasonCodeId;
    }

    public void setAbsenceReasonCodeId(Long absenceReasonCodeId) {
        this.absenceReasonCodeId = absenceReasonCodeId;
    }

    public boolean isBreakReplaced() {
        return breakReplaced;
    }

    public void setBreakReplaced(boolean breakReplaced) {
        this.breakReplaced = breakReplaced;
    }

    public List<TimeBankCTADistribution> getTimeBankCTADistributions() {
        return timeBankCTADistributions;
    }

    public void setTimeBankCTADistributions(List<TimeBankCTADistribution> timeBankCTADistributions) {
        this.timeBankCTADistributions = timeBankCTADistributions;
    }

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public int getPlannedMinutesOfTimebank() {
        return plannedMinutesOfTimebank;
    }

    public void setPlannedMinutesOfTimebank(int plannedMinutesOfTimebank) {
        this.plannedMinutesOfTimebank = plannedMinutesOfTimebank;
    }

    public List<PayOutPerShiftCTADistribution> getPayoutPerShiftCTADistributions() {
        return payoutPerShiftCTADistributions;
    }

    public void setPayoutPerShiftCTADistributions(List<PayOutPerShiftCTADistribution> payoutPerShiftCTADistributions) {
        this.payoutPerShiftCTADistributions = isNullOrElse(payoutPerShiftCTADistributions,new ArrayList<>());
    }

    public int getPayoutCtaBonusMinutes() {
        return payoutCtaBonusMinutes;
    }

    public void setPayoutCtaBonusMinutes(int payoutCtaBonusMinutes) {
        this.payoutCtaBonusMinutes = payoutCtaBonusMinutes;
    }

    public int getPlannedMinutesOfPayout() {
        return plannedMinutesOfPayout;
    }

    public void setPlannedMinutesOfPayout(int plannedMinutesOfPayout) {
        this.plannedMinutesOfPayout = plannedMinutesOfPayout;
    }

    public int getScheduledMinutesOfTimebank() {
        return scheduledMinutesOfTimebank;
    }

    public void setScheduledMinutesOfTimebank(int scheduledMinutesOfTimebank) {
        this.scheduledMinutesOfTimebank = scheduledMinutesOfTimebank;
    }

    public int getScheduledMinutesOfPayout() {
        return scheduledMinutesOfPayout;
    }

    public void setScheduledMinutesOfPayout(int scheduledMinutesOfPayout) {
        this.scheduledMinutesOfPayout = scheduledMinutesOfPayout;
    }
}
