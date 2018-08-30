package com.kairos.activity.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;

import java.util.List;

public class ActivityAndShiftStatusWrapper {
    private ShiftStatus status;
    private List<ActivityAndShiftStatusSettingsDTO> activityAndShiftStatusSettings;

    public ActivityAndShiftStatusWrapper() {
        //Default Constructor
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    public List<ActivityAndShiftStatusSettingsDTO> getActivityAndShiftStatusSettings() {
        return activityAndShiftStatusSettings;
    }

    public void setActivityAndShiftStatusSettings(List<ActivityAndShiftStatusSettingsDTO> activityAndShiftStatusSettings) {
        this.activityAndShiftStatusSettings = activityAndShiftStatusSettings;
    }
}
