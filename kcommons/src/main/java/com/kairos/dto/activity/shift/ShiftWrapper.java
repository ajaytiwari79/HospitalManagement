package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;

import java.util.List;

/**
 * Created by vipul on 11/5/18.
 */
public class ShiftWrapper {
    private List<ShiftDTO> assignedShifts;
    private List<OpenShiftResponseDTO> openShifts;
    private StaffAccessRoleDTO staffDetails;

    public ShiftWrapper() {
        //default case
    }

    public ShiftWrapper(List<ShiftDTO> assignedShifts, List<OpenShiftResponseDTO> openShifts) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
    }

    public ShiftWrapper(List<ShiftDTO> assignedShifts, List<OpenShiftResponseDTO> openShifts, StaffAccessRoleDTO staffDetails) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
        this.staffDetails = staffDetails;
    }

    public List<ShiftDTO> getAssignedShifts() {
        return assignedShifts;
    }

    public void setAssignedShifts(List<ShiftDTO> assignedShifts) {
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
