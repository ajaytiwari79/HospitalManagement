package com.kairos.dto.activity.activity.activity_tabs.communication_tab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 24/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CommunicationActivityDTO {
    private BigInteger activityId;
    private boolean allowCommunicationReminder;
    private boolean notifyAfterDeleteActivity;
    private List<ActivityReminderSettings> activityReminderSettings;
    private boolean allowActivityCutoffReminder;
    private List<ActivityReminderSettings> activityCutoffReminderSettings;
}
