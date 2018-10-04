package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.time.LocalTime;
import java.util.List;

/**
 * @author pradeep
 * @date - 14/9/18
 */

public class ShiftDetailViewDTO {

    private List<ShiftDTO> plannedShifts;
    private List<ShiftDTO> realTimeShifts;
    private List<ShiftDTO> staffValidated;
    private List<ShiftDTO> plannerValidated;


    public ShiftDetailViewDTO(List<ShiftDTO> plannedShifts, List<ShiftDTO> realTimeShifts, List<ShiftDTO> staffValidated, List<ShiftDTO> plannerValidated) {
        this.plannedShifts = plannedShifts;
        this.realTimeShifts = realTimeShifts;
        this.staffValidated = staffValidated;
        this.plannerValidated = plannerValidated;

    }

    public ShiftDetailViewDTO() {
    }



    public List<ShiftDTO> getPlannedShifts() {
        return plannedShifts;
    }

    public void setPlannedShifts(List<ShiftDTO> plannedShifts) {
        this.plannedShifts = plannedShifts;
    }

    public List<ShiftDTO> getRealTimeShifts() {
        return realTimeShifts;
    }

    public void setRealTimeShifts(List<ShiftDTO> realTimeShifts) {
        this.realTimeShifts = realTimeShifts;
    }

    public List<ShiftDTO> getStaffValidated() {
        return staffValidated;
    }

    public void setStaffValidated(List<ShiftDTO> staffValidated) {
        this.staffValidated = staffValidated;
    }

    public List<ShiftDTO> getPlannerValidated() {
        return plannerValidated;
    }

    public void setPlannerValidated(List<ShiftDTO> plannerValidated) {
        this.plannerValidated = plannerValidated;
    }

}
