package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vipul on 24/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommunicationActivityTab implements Serializable {
    private boolean allowCommunicationReminder;
    private boolean notifyAfterDeleteActivity;
    private List<ActivityReminderSettings> activityReminderSettings;
    private boolean allowActivityCutoffReminder;
    private List<ActivityReminderSettings> activityCutoffReminderSettings;

    public CommunicationActivityTab(boolean allowCommunicationReminder, boolean allowActivityCutoffReminder, boolean notifyAfterDeleteActivity) {
        this.allowCommunicationReminder = allowCommunicationReminder;
        this.allowActivityCutoffReminder = allowActivityCutoffReminder;
        this.notifyAfterDeleteActivity = notifyAfterDeleteActivity;
    }

}
