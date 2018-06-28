package com.kairos.activity.shift;

import com.kairos.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.user.staff.staff.StaffAccessRoleDTO;

import java.util.List;

/**
 * Created by vipul on 11/5/18.
 */
public class ShiftWrapper {
    private List<ShiftQueryResult> assignedShifts;
    private List<OpenShiftResponseDTO> openShifts;
    private StaffAccessRoleDTO staffDetails;

    public ShiftWrapper() {
        //default case
    }

    public ShiftWrapper(List<ShiftQueryResult> assignedShifts, List<OpenShiftResponseDTO> openShifts) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
    }

    public ShiftWrapper(List<ShiftQueryResult> assignedShifts, List<OpenShiftResponseDTO> openShifts, StaffAccessRoleDTO staffDetails) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
        this.staffDetails = staffDetails;
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

    public StaffAccessRoleDTO getStaffDetails() {
        return staffDetails;
    }

    public void setStaffDetails(StaffAccessRoleDTO staffDetails) {
        this.staffDetails = staffDetails;
    }
}
