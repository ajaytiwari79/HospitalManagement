package com.kairos.activity.time_bank;

import com.kairos.activity.time_bank.time_bank_basic.time_bank.ScheduledActivitiesDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 17/8/18
 */

public class TimeBankVisualViewDTO {

    private long  timeBankMinutes;
    private long presenceScheduledMinutes;
    private long absenceScheduledMinutes;
    private long timeBankChange;

    private List<TimeBankIntervalDTO> timeBankIntervals;
    private List<ScheduledActivitiesDTO> scheduledActivities;
    private List<TimeBankCTADistributionDTO> timeBankCTADistributions;


    public List<TimeBankIntervalDTO> getTimeBankIntervals() {
        return timeBankIntervals;
    }

    public void setTimeBankIntervals(List<TimeBankIntervalDTO> timeBankIntervals) {
        this.timeBankIntervals = timeBankIntervals;
    }

    public long getTimeBankMinutes() {
        return timeBankMinutes;
    }

    public void setTimeBankMinutes(long timeBankMinutes) {
        this.timeBankMinutes = timeBankMinutes;
    }

    public long getPresenceScheduledMinutes() {
        return presenceScheduledMinutes;
    }

    public void setPresenceScheduledMinutes(long presenceScheduledMinutes) {
        this.presenceScheduledMinutes = presenceScheduledMinutes;
    }

    public long getAbsenceScheduledMinutes() {
        return absenceScheduledMinutes;
    }

    public void setAbsenceScheduledMinutes(long absenceScheduledMinutes) {
        this.absenceScheduledMinutes = absenceScheduledMinutes;
    }

    public long getTimeBankChange() {
        return timeBankChange;
    }

    public void setTimeBankChange(long timeBankChange) {
        this.timeBankChange = timeBankChange;
    }

    public List<ScheduledActivitiesDTO> getScheduledActivities() {
        return scheduledActivities;
    }

    public void setScheduledActivities(List<ScheduledActivitiesDTO> scheduledActivities) {
        this.scheduledActivities = scheduledActivities;
    }

    public List<TimeBankCTADistributionDTO> getTimeBankCTADistributions() {
        return timeBankCTADistributions;
    }

    public void setTimeBankCTADistributions(List<TimeBankCTADistributionDTO> timeBankCTADistributions) {
        this.timeBankCTADistributions = timeBankCTADistributions;
    }
}
