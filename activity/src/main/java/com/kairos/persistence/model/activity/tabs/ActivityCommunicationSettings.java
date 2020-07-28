package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 24/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class ActivityCommunicationSettings {
    @KPermissionField
    private boolean allowCommunicationReminder;
    @KPermissionField
    private boolean notifyAfterDeleteActivity;
    @KPermissionField
    private List<ActivityReminderSettings> activityReminderSettings;
    @KPermissionField
    private boolean allowActivityCutoffReminder;
    @KPermissionField
    private List<ActivityReminderSettings> activityCutoffReminderSettings;

    public ActivityCommunicationSettings(boolean allowCommunicationReminder, boolean allowActivityCutoffReminder, boolean notifyAfterDeleteActivity) {
        this.allowCommunicationReminder = allowCommunicationReminder;
        this.allowActivityCutoffReminder = allowActivityCutoffReminder;
        this.notifyAfterDeleteActivity = notifyAfterDeleteActivity;
    }

}
