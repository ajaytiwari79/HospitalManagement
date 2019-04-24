package com.kairos.dto.activity.activity.activity_tabs.communication_tab;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * CreatedBy vipulpandey on 6/10/18
 **/
public class ActivityReminderSettings implements Serializable,Comparator<ActivityReminderSettings> {
    private byte sequence;
    private BigInteger id;
    private FrequencySettings sendReminder; // this is used for settings before days and value settings
    private boolean repeatAllowed;
    private FrequencySettings repeatReminder;

    public ActivityReminderSettings() {
        // DC
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public byte getSequence() {
        return sequence;
    }

    public void setSequence(byte sequence) {
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

    @Override
    public String toString() {
        return "ActivityReminderSettings{" +
                "sequence=" + sequence +
                ", sendReminder=" + sendReminder +
                ", repeatAllowed=" + repeatAllowed +
                ", repeatReminder=" + repeatReminder +
                '}';
    }
}
