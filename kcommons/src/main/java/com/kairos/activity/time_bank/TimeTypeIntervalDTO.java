package com.kairos.activity.time_bank;

import java.util.Date;

public class TimeTypeIntervalDTO {


    private Date startDate;
    private Date endDate;
    private int minutes;


    public TimeTypeIntervalDTO(Date startDate, Date endDate, int minutes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.minutes = minutes;
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

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
