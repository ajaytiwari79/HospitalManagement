package com.kairos.dto.activity.activity.activity_tabs.communication_tab;

import com.kairos.enums.DurationType;

import java.io.Serializable;
import java.util.Comparator;

/**
 * CreatedBy vipulpandey on 6/10/18
 **/
public class ActivityReminderSettings implements Serializable,Comparator<ActivityReminderSettings> {
    private Byte sequence;
    private FrequencySettings sendReminder; // this is used for settings before days and value settings
    private boolean repeatAllowed;
    private FrequencySettings repeatReminder;

    public ActivityReminderSettings() {
        // DC
    }

    public Byte getSequence() {
        return sequence;
    }

    public void setSequence(Byte sequence) {
        this.sequence = sequence;
    }

    public FrequencySettings getSendReminder() {
        return sendReminder;
    }

    public void setSendReminder(FrequencySettings sendReminder) {
        this.sendReminder = sendReminder;
    }

    public boolean isRepeatAllowed() {
        return repeatAllowed;
    }

    public void setRepeatAllowed(boolean repeatAllowed) {
        this.repeatAllowed = repeatAllowed;
    }

    public FrequencySettings getRepeatReminder() {
        return repeatReminder;
    }

    public void setRepeatReminder(FrequencySettings repeatReminder) {
        this.repeatReminder = repeatReminder;
    }
    @Override
    public int compare(ActivityReminderSettings first, ActivityReminderSettings second) {
        return first.getSequence()-second.getSequence();
    }
}
