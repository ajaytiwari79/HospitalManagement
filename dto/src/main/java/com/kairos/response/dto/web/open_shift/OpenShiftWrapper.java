package com.kairos.response.dto.web.open_shift;

import com.kairos.activity.shift.ShiftQueryResult;

import java.util.List;

public class OpenShiftWrapper {
    private int timeBank;
    private int plannedTime;
    private int restingTime;
    private List<ShiftQueryResult> similarShifts;

    public OpenShiftWrapper() {
        //Default Constructor
    }

    public OpenShiftWrapper(int timeBank, int plannedTime, int restingTime, List<ShiftQueryResult> similarShifts) {
        this.timeBank = timeBank;
        this.plannedTime = plannedTime;
        this.restingTime = restingTime;
        this.similarShifts = similarShifts;
    }

    public int getTimeBank() {
        return timeBank;
    }

    public void setTimeBank(int timeBank) {
        this.timeBank = timeBank;
    }

    public int getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(int plannedTime) {
        this.plannedTime = plannedTime;
    }

    public int getRestingTime() {
        return restingTime;
    }

    public void setRestingTime(int restingTime) {
        this.restingTime = restingTime;
    }

    public List<ShiftQueryResult> getSimilarShifts() {
        return similarShifts;
    }

    public void setSimilarShifts(List<ShiftQueryResult> similarShifts) {
        this.similarShifts = similarShifts;
    }
}
