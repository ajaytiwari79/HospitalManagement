package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by vipul on 24/8/17.
 */
public class CommunicationActivityTab implements Serializable{

    private boolean allowCommunicationReminder;
    private String timeUnit;
    private long timeLength;
    private  boolean notifyAfterDeleteActivityType;

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

    public CommunicationActivityTab(boolean allowCommunicationReminder, String timeUnit, long timeLength, boolean notifyAfterDeleteActivityType) {
        this.allowCommunicationReminder = allowCommunicationReminder;
        this.timeUnit = timeUnit;
        this.notifyAfterDeleteActivityType=notifyAfterDeleteActivityType;
        this.timeLength = timeLength;
    }

}
