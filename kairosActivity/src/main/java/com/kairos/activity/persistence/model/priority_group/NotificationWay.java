package com.kairos.activity.persistence.model.priority_group;

public class NotificationWay {
    private boolean allowOneWaySMS;
    private boolean allowTwoWaySMS;
    private boolean allowMobilePushNotification;
    private boolean allowTODONotification;
    private boolean allowFeedNotification;

    public NotificationWay() {
        //Default Constructor
    }

    public NotificationWay(boolean allowOneWaySMS, boolean allowTwoWaySMS, boolean allowMobilePushNotification, boolean allowTODONotification, boolean allowFeedNotification) {
        this.allowOneWaySMS = allowOneWaySMS;
        this.allowTwoWaySMS = allowTwoWaySMS;
        this.allowMobilePushNotification = allowMobilePushNotification;
        this.allowTODONotification = allowTODONotification;
        this.allowFeedNotification = allowFeedNotification;
    }

    public boolean isAllowOneWaySMS() {
        return allowOneWaySMS;
    }

    public void setAllowOneWaySMS(boolean allowOneWaySMS) {
        this.allowOneWaySMS = allowOneWaySMS;
    }

    public boolean isAllowTwoWaySMS() {
        return allowTwoWaySMS;
    }

    public void setAllowTwoWaySMS(boolean allowTwoWaySMS) {
        this.allowTwoWaySMS = allowTwoWaySMS;
    }

    public boolean isAllowMobilePushNotification() {
        return allowMobilePushNotification;
    }

    public void setAllowMobilePushNotification(boolean allowMobilePushNotification) {
        this.allowMobilePushNotification = allowMobilePushNotification;
    }

    public boolean isAllowTODONotification() {
        return allowTODONotification;
    }

    public void setAllowTODONotification(boolean allowTODONotification) {
        this.allowTODONotification = allowTODONotification;
    }

    public boolean isAllowFeedNotification() {
        return allowFeedNotification;
    }

    public void setAllowFeedNotification(boolean allowFeedNotification) {
        this.allowFeedNotification = allowFeedNotification;
    }
}
