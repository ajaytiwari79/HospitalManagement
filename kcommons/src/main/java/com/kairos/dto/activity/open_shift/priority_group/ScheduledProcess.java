package com.kairos.dto.activity.open_shift.priority_group;

import com.kairos.enums.DayCheckPeriod;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

public class ScheduledProcess {
    private DayCheckPeriod dayCheckPeriod; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Integer startMinute;//
    private Integer selectedRepeatInterval;//
    private Set<DayOfWeek> selectedDays;//
    private Integer runOnce;//
    private List<String> selectedTimes;//




    public ScheduledProcess() {
        //Default Constructor
    }

    public ScheduledProcess(DayCheckPeriod dayCheckPeriod, Integer startMinute, Integer selectedRepeatInterval,
                            Set<DayOfWeek> selectedDays, Integer runOnce, List<String> selectedTimes) {
        this.dayCheckPeriod = dayCheckPeriod;
        this.startMinute = startMinute;
        this.selectedRepeatInterval = selectedRepeatInterval;
        this.selectedDays = selectedDays;
        this.runOnce = runOnce;
        this.selectedTimes = selectedTimes;
    }

    public DayCheckPeriod getDayCheckPeriod() {
        return dayCheckPeriod;
    }

    public void setDayCheckPeriod(DayCheckPeriod dayCheckPeriod) {
        this.dayCheckPeriod = dayCheckPeriod;
    }

    public Integer getSelectedRepeatInterval() {
        return selectedRepeatInterval;
    }

    public void setSelectedRepeatInterval(Integer selectedRepeatInterval) {
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

    public Integer getRepeat() {
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

    public Integer getRunOnce() {
        return runOnce;
    }

    public void setRunOnce(Integer runOnce) {
        this.runOnce = runOnce;
    }

    public List<String> getSelectedHours() {
        return selectedTimes;
    }

    public void setSelectedHours(List<String> selectedTimes) {
        this.selectedTimes = selectedTimes;
    }

}
