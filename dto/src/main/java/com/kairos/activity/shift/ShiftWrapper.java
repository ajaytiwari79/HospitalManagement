package com.kairos.activity.shift;

import java.util.List;

/**
 * Created by vipul on 11/5/18.
 */
public class ShiftWrapper {
    private List<ShiftQueryResult> assignedShifts;
    private List<ShiftQueryResult> openShifts;

    public ShiftWrapper() {
        //default case
    }

    public ShiftWrapper(List<ShiftQueryResult> assignedShifts, List<ShiftQueryResult> openShifts) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
    }

    public List<ShiftQueryResult> getAssignedShifts() {
        return assignedShifts;
    }

    public void setAssignedShifts(List<ShiftQueryResult> assignedShifts) {
        this.assignedShifts = assignedShifts;
    }

    public List<ShiftQueryResult> getOpenShifts() {
        return openShifts;
    }

    public void setOpenShifts(List<ShiftQueryResult> openShifts) {
        this.openShifts = openShifts;
    }
}
