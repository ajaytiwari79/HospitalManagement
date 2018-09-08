package com.kairos.dto.activity.open_shift;

import com.kairos.enums.InformationReciever;

import java.util.List;

public class PlannerNotificationInfo {
        private List<InformationReciever> informationReceivers;
        private DurationField sendNotificationOnCandidateMissingBeforeTime; //in Hours
        private DurationField notifyForUnassignedShiftBeforeTime; // in Hours

    public PlannerNotificationInfo() {
        //Default Constructor
    }

    public DurationField getSendNotificationOnCandidateMissingBeforeTime() {
        return sendNotificationOnCandidateMissingBeforeTime;
    }

    public void setSendNotificationOnCandidateMissingBeforeTime(DurationField sendNotificationOnCandidateMissingBeforeTime) {
        this.sendNotificationOnCandidateMissingBeforeTime = sendNotificationOnCandidateMissingBeforeTime;
    }

    public DurationField getNotifyForUnassignedShiftBeforeTime() {
        return notifyForUnassignedShiftBeforeTime;
    }

    public void setNotifyForUnassignedShiftBeforeTime(DurationField notifyForUnassignedShiftBeforeTime) {
        this.notifyForUnassignedShiftBeforeTime = notifyForUnassignedShiftBeforeTime;
    }

    public List<InformationReciever> getInformationReceivers() {
        return informationReceivers;
    }

    public void setInformationReceivers(List<InformationReciever> informationReceivers) {
        this.informationReceivers = informationReceivers;
    }
}
