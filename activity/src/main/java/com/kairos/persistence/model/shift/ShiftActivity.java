package com.kairos.persistence.model.shift;

import com.kairos.commons.audit_logging.IgnoreLogging;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.roundDateByMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;

/**
 * @author pradeep
 * @date - 10/9/18
 */
@Getter
@Setter
@NoArgsConstructor
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
    private boolean breakReplaced;
    private List<TimeBankCTADistribution> timeBankCTADistributions;
    private List<PayOutPerShiftCTADistribution> payoutPerShiftCTADistributions;
    private int payoutCtaBonusMinutes;
    private int timeBankCtaBonusMinutes;
    private String startLocation; // this is for the location from where activity will gets starts
    private String endLocation;   // this is for the location from where activity will gets ends
    private int plannedMinutesOfTimebank;
    private int plannedMinutesOfPayout;
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private List<PlannedTime> plannedTimes;
    private List<ShiftActivity> childActivities;
    private boolean breakNotHeld;
    private Set<ShiftStatus> status = new HashSet<>();
    private transient BigInteger plannedTimeId;
    private boolean breakInterrupt;


    @IgnoreLogging
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getStartDate().getTime(), this.getEndDate().getTime());
    }


    public ShiftActivity( String activityName,Date startDate, Date endDate,BigInteger activityId,String timeType) {
        this.activityId = activityId;
        this.startDate = roundDateByMinutes(startDate,15);;
        this.endDate = roundDateByMinutes(endDate,15);
        this.activityName = activityName;
        this.timeType = timeType;
    }

    public ShiftActivity(BigInteger activityId, String activityName) {
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public ShiftActivity(BigInteger activityId, Date startDate,Date endDate) {
        this.activityId = activityId;
        this.startDate=roundDateByMinutes(startDate,15);
        this.endDate=roundDateByMinutes(endDate,15);;
    }

    public void setPayoutPerShiftCTADistributions(List<PayOutPerShiftCTADistribution> payoutPerShiftCTADistributions) {
        this.payoutPerShiftCTADistributions = isNullOrElse(payoutPerShiftCTADistributions,new ArrayList<>());
    }

    public List<PlannedTime> getPlannedTimes() {
        return plannedTimes=Optional.ofNullable(plannedTimes).orElse(new ArrayList<>());
    }

    public List<ShiftActivity> getChildActivities() {
        return this.childActivities = isNullOrElse(this.childActivities,new ArrayList<>());
    }

    public void setChildActivities(List<ShiftActivity> childActivities) {
        this.childActivities = isNullOrElse(childActivities,new ArrayList<>());
    }

    public Set<ShiftStatus> getStatus() {
        return isNullOrElse(status,newHashSet());
    }

    @Override
    public int compareTo(ShiftActivity shiftActivity) {
        return this.startDate.compareTo(shiftActivity.startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = roundDateByMinutes(startDate,15);
    }

    public void setEndDate(Date endDate) {
        this.endDate = roundDateByMinutes(endDate,15);
    }

    public boolean isShiftActivityChanged(ShiftActivity shiftActivity){
        if(!isEquals(startDate,shiftActivity.getStartDate())){
            return true;
        }
        DateTimeInterval thisInterVal=new DateTimeInterval(startDate,endDate);
        DateTimeInterval shiftActivityInterval=new DateTimeInterval(shiftActivity.getStartDate(),shiftActivity.getEndDate());
        if(!thisInterVal.equals(shiftActivityInterval) || !activityId.equals(shiftActivity.getActivityId()) || this.getDurationMinutes() != shiftActivity.getDurationMinutes()){
            return true;
        }
        if (this.getChildActivities().size() != shiftActivity.getChildActivities().size()) {
            return true;
        }
        for (int i = 0; i <this.getChildActivities().size() ; i++) {
            ShiftActivity thisChildActivity=this.getChildActivities().get(i);
            ShiftActivity childActivity=shiftActivity.getChildActivities().get(i);
            if(thisChildActivity.isShiftActivityChanged(childActivity)){
                return true;
            }
        }
        return false;
    }
}
