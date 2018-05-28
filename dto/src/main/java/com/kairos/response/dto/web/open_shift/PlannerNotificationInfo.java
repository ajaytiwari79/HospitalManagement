package com.kairos.response.dto.web.open_shift;

import com.kairos.persistence.model.enums.InformationReciever;

import java.util.List;

public class PlannerNotificationInfo {
        private List<InformationReciever> informationReceivers;
        private DurationFields sendNotificationOnCandidateMissingBeforeTime; //in Hours
        private DurationFields notifyForUnassignedShiftBeforeTime; // in Hours

    public PlannerNotificationInfo() {
        //Default Constructor
    }

    public DurationFields getSendNotificationOnCandidateMissingBeforeTime() {
        return sendNotificationOnCandidateMissingBeforeTime;
    }

    public void setSendNotificationOnCandidateMissingBeforeTime(DurationFields sendNotificationOnCandidateMissingBeforeTime) {
        this.sendNotificationOnCandidateMissingBeforeTime = sendNotificationOnCandidateMissingBeforeTime;
    }

    public DurationFields getNotifyForUnassignedShiftBeforeTime() {
        return notifyForUnassignedShiftBeforeTime;
    }

    public void setNotifyForUnassignedShiftBeforeTime(DurationFields notifyForUnassignedShiftBeforeTime) {
        this.notifyForUnassignedShiftBeforeTime = notifyForUnassignedShiftBeforeTime;
    }

    public List<InformationReciever> getInformationReceivers() {
        return informationReceivers;
    }

    public void setInformationReceivers(List<InformationReciever> informationReceivers) {
        this.informationReceivers = informationReceivers;
    }
}
