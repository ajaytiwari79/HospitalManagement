package com.kairos.activity.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;

import java.util.List;

public class ActivityAndShiftStatusWrapper {
    private ShiftStatus status;
    private List<ActivityAndShiftStatusSettingsDTO> rowData;

    public ActivityAndShiftStatusWrapper() {
        //Default Constructor
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    public List<ActivityAndShiftStatusSettingsDTO> getRowData() {
        return rowData;
    }

    public void setRowData(List<ActivityAndShiftStatusSettingsDTO> rowData) {
        this.rowData = rowData;
    }
}
