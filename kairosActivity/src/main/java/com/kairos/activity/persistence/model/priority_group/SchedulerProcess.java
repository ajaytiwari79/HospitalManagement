package com.kairos.activity.persistence.model.priority_group;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SchedulerProcess {
    private String interval; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Date lastRunTime;
    private Date nextRunTime;
    private Integer startMinute;
    private String repeat;
    private Set<DayOfWeek> days;
    private String runOnce;
    private List<String> selectedHours;
    private Date startDate;
    private Date endDate;
    private Integer weeks;

    public SchedulerProcess() {
        //Default Constructor
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Set<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(Set<DayOfWeek> days) {
        this.days = days;
    }

    public String getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(String runOnce) {
        this.runOnce = runOnce;
    }

    public List<String> getSelectedHours() {
        return selectedHours;
    }

    public void setSelectedHours(List<String> selectedHours) {
        this.selectedHours = selectedHours;
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

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }
}
