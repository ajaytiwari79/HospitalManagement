package com.kairos.response.dto.web.open_shift;

public class PlannerNotificationInfo {
        private Integer missingPersonNotifyBefore; //in days
        private Integer notifyForUnassignedShiftBefore; // in days

    public PlannerNotificationInfo() {
        //Default Constructor
    }

    public Integer getMissingPersonNotifyBefore() {
        return missingPersonNotifyBefore;
    }

    public void setMissingPersonNotifyBefore(Integer missingPersonNotifyBefore) {
        this.missingPersonNotifyBefore = missingPersonNotifyBefore;
    }

    public Integer getNotifyForUnassignedShiftBefore() {
        return notifyForUnassignedShiftBefore;
    }

    public void setNotifyForUnassignedShiftBefore(Integer notifyForUnassignedShiftBefore) {
        this.notifyForUnassignedShiftBefore = notifyForUnassignedShiftBefore;
    }
}
