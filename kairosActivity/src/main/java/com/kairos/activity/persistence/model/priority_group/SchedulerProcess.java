package com.kairos.activity.persistence.model.priority_group;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class SchedulerProcess {
    private String dayCheckPeriod; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Integer startMinute;//
    private String selectedRepeatInterval;//
    private Set<DayOfWeek> selectedDays;//
    private String runOnce;//
    private List<String> selectedTimes;//


    public SchedulerProcess() {
        //Default Constructor
    }

    public String getDayCheckPeriod() {
        return dayCheckPeriod;
    }

    public void setDayCheckPeriod(String dayCheckPeriod) {
        this.dayCheckPeriod = dayCheckPeriod;
    }

    public String getSelectedRepeatInterval() {
        return selectedRepeatInterval;
    }

    public void setSelectedRepeatInterval(String selectedRepeatInterval) {
        this.selectedRepeatInterval = selectedRepeatInterval;
    }

    public Set<DayOfWeek> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(Set<DayOfWeek> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public List<String> getSelectedTimes() {
        return selectedTimes;
    }

    public void setSelectedTimes(List<String> selectedTimes) {
        this.selectedTimes = selectedTimes;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public String getRepeat() {
        return selectedRepeatInterval;
    }

    public void setRepeat(String repeat) {
        this.selectedRepeatInterval = selectedRepeatInterval;
    }

    public Set<DayOfWeek> getDays() {
        return selectedDays;
    }

    public void setDays(Set<DayOfWeek> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public String getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(String runOnce) {
        this.runOnce = runOnce;
    }

    public List<String> getSelectedHours() {
        return selectedTimes;
    }

    public void setSelectedHours(List<String> selectedTimes) {
        this.selectedTimes = selectedTimes;
    }

}
