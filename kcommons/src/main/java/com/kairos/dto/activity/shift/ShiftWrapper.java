package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 11/5/18.
 */
public class ShiftWrapper {
    private List<ShiftDTO> assignedShifts;
    private List<OpenShiftResponseDTO> openShifts;
    private StaffAccessRoleDTO staffDetails;
    private ButtonConfig buttonConfig;
    private Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj;

    public ShiftWrapper() {
        //default case
    }

    public ShiftWrapper(List<ShiftDTO> assignedShifts, List<OpenShiftResponseDTO> openShifts, StaffAccessRoleDTO staffDetails,ButtonConfig buttonConfig,Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj) {
        this.assignedShifts = assignedShifts;
        this.openShifts = openShifts;
        this.staffDetails = staffDetails;
        this.buttonConfig = buttonConfig;
        this.assignedFunctionsObj=assignedFunctionsObj;
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

    public ButtonConfig getButtonConfig() {
        return buttonConfig;
    }

    public void setButtonConfig(ButtonConfig buttonConfig) {
        this.buttonConfig = buttonConfig;
    }

    public Map<LocalDate,List<FunctionDTO>> getAssignedFunctionsObj() {
        return assignedFunctionsObj;
    }

    public void setAssignedFunctionsObj(Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj) {
        this.assignedFunctionsObj = assignedFunctionsObj;
    }
}
