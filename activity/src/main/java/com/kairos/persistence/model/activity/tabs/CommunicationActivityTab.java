package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vipul on 24/8/17.
 */
public class CommunicationActivityTab implements Serializable {
    private boolean allowCommunicationReminder;
    private boolean notifyAfterDeleteActivity;
    private List<ActivityReminderSettings> activityReminderSettings;

    public CommunicationActivityTab() {
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

    public CommunicationActivityTab(boolean allowCommunicationReminder, boolean notifyAfterDeleteActivity) {
        this.allowCommunicationReminder = allowCommunicationReminder;
        this.notifyAfterDeleteActivity = notifyAfterDeleteActivity;
    }


}
