package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.ShiftStatus;

import java.util.List;

/**
 * Created by vipul on 9/5/18.
 */
public class ShiftPublishDTO {
    private List<ShiftActivitiesIdDTO> shifts;
    private ShiftStatus status;


    public ShiftPublishDTO() {
        //default
    }

    public ShiftPublishDTO(List<ShiftActivitiesIdDTO> shifts, ShiftStatus status) {
        this.shifts = shifts;
        this.status = status;
    }

    public List<ShiftActivitiesIdDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftActivitiesIdDTO> shifts) {
        this.shifts = shifts;
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }
}
