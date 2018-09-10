package com.kairos.dto.activity.shift;

import java.math.BigInteger;
import java.util.Date;

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
    private String activityname;
    private int order;


    public ShiftActivity( String activityname,Date startDate, Date endDate,BigInteger activityId) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityname = activityname;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }
}
