package com.kairos.activity.open_shift;

import com.kairos.activity.shift.ShiftQueryResult;

import java.util.List;

public class OpenShiftWrapper {
    private int timeBank;
    private int plannedTime;
    private int restingTime;
    private List<OpenShiftResponseDTO> similarOpenShifts;
    private OpenShiftResponseDTO openShift;


    public OpenShiftWrapper() {
        //Default Constructor
    }

    public OpenShiftWrapper(int timeBank, int plannedTime, int restingTime, List<OpenShiftResponseDTO> similarOpenShifts) {
        this.timeBank = timeBank;
        this.plannedTime = plannedTime;
        this.restingTime = restingTime;
        this.similarOpenShifts = similarOpenShifts;

    }

    public OpenShiftWrapper(int timeBank, int plannedTime, int restingTime, List<OpenShiftResponseDTO> similarOpenShifts, OpenShiftResponseDTO openShift) {
        this.timeBank = timeBank;
        this.plannedTime = plannedTime;
        this.restingTime = restingTime;
        this.similarOpenShifts = similarOpenShifts;
        this.openShift = openShift;
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

    public List<OpenShiftResponseDTO> getSimilarOpenShifts() {
        return similarOpenShifts;
    }

    public void setSimilarOpenShifts(List<OpenShiftResponseDTO> similarOpenShifts) {
        this.similarOpenShifts = similarOpenShifts;
    }

    public OpenShiftResponseDTO getOpenShift() {
        return openShift;
    }

    public void setOpenShift(OpenShiftResponseDTO openShift) {
        this.openShift = openShift;
    }
}
