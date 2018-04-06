package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.CommunicationActivityTab;

import java.math.BigInteger;

/**
 * Created by vipul on 24/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunicationActivityDTO {
    private BigInteger activityId;
    private boolean allowCommunicationReminder;
    private String timeUnit;
    private long timeLength;
    private boolean notifyAfterDeleteActivityType;

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isAllowCommunicationReminder() {
        return allowCommunicationReminder;
    }

    public void setAllowCommunicationReminder(boolean allowCommunicationReminder) {
        this.allowCommunicationReminder = allowCommunicationReminder;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    public boolean isNotifyAfterDeleteActivityType() {
        return notifyAfterDeleteActivityType;
    }

    public void setNotifyAfterDeleteActivityType(boolean notifyAfterDeleteActivityType) {
        this.notifyAfterDeleteActivityType = notifyAfterDeleteActivityType;
    }

    public CommunicationActivityTab buildSMSReminderActivityTab(){
        CommunicationActivityTab communicationActivityTab =new CommunicationActivityTab(allowCommunicationReminder,timeUnit,timeLength,notifyAfterDeleteActivityType);
        return communicationActivityTab;
    }
}
