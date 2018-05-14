package com.kairos.activity.shift;

import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;

import java.util.List;

/**
 * Created by vipul on 11/5/18.
 */
public class ShiftWrapper {
    private List<ShiftQueryResult> assignedShifts;
    private List<OpenShiftResponseDTO> openShifts;

    public ShiftWrapper() {
        //default case
    }

    public ShiftWrapper(List<ShiftQueryResult> assignedShifts, List<OpenShiftResponseDTO> openShifts) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
    }

    public List<ShiftQueryResult> getAssignedShifts() {
        return assignedShifts;
    }

    public void setAssignedShifts(List<ShiftQueryResult> assignedShifts) {
        this.assignedShifts = assignedShifts;
    }

    public List<OpenShiftResponseDTO> getOpenShifts() {
        return openShifts;
    }

    public void setOpenShifts(List<OpenShiftResponseDTO> openShifts) {
        this.openShifts = openShifts;
    }
}
