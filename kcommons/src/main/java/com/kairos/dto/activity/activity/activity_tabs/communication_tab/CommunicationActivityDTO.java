package com.kairos.dto.activity.activity.activity_tabs.communication_tab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 24/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunicationActivityDTO {
    private BigInteger activityId;
    private boolean allowCommunicationReminder;
    private boolean notifyAfterDeleteActivity;
    private List<ActivityReminderSettings> activityReminderSettings;

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

    public boolean isNotifyAfterDeleteActivity() {
        return notifyAfterDeleteActivity;
    }

    public void setNotifyAfterDeleteActivity(boolean notifyAfterDeleteActivity) {
        this.notifyAfterDeleteActivity = notifyAfterDeleteActivity;
    }

    public List<ActivityReminderSettings> getActivityReminderSettings() {
        return activityReminderSettings;
    }

    public void setActivityReminderSettings(List<ActivityReminderSettings> activityReminderSettings) {
        this.activityReminderSettings = activityReminderSettings;
    }
}
