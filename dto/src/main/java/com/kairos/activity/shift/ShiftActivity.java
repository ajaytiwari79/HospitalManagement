package com.kairos.activity.shift;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author pradeep
 * @date - 7/9/18
 */

public class ShiftActivity {

    private String activityName;
    private Date startDate;
    private Date endDate;
    private BigInteger activityId;
    private int scheduledMinutes;
    private int durationMinutes;
    public ShiftActivity() {
    }

    public ShiftActivity(String activityName, Date startDate, Date endDate, BigInteger activityId) {
        this.activityName = activityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityId = activityId;
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
    public BigInteger getActivityId() {
        return activityId;
    }
    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

}
