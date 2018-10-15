package com.kairos.dto.activity.activity.activity_tabs;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;
import java.util.Set;


public class ActivityShiftStatusSettings {

    private ShiftStatus shiftStatus;
    private Set<Long> accessGroupIds;

    public ActivityShiftStatusSettings() {
        //Default Constructor
    }

    public ActivityShiftStatusSettings(ShiftStatus shiftStatus, Set<Long> accessGroupIds) {
        this.shiftStatus = shiftStatus;
        this.accessGroupIds = accessGroupIds;
    }

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }

    public Set<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(Set<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }

}
