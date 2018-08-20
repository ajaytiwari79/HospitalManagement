package com.kairos.activity.time_bank;

import com.kairos.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
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
    private List<CTADistributionDTO> timeBankCTADistributions;


    public TimeBankVisualViewDTO(long timeBankMinutes, long presenceScheduledMinutes, long absenceScheduledMinutes, long timeBankChange, List<TimeBankIntervalDTO> timeBankIntervals, List<ScheduledActivitiesDTO> scheduledActivities, List<CTADistributionDTO> timeBankCTADistributions) {
        this.timeBankMinutes = timeBankMinutes;
        this.presenceScheduledMinutes = presenceScheduledMinutes;
        this.absenceScheduledMinutes = absenceScheduledMinutes;
        this.timeBankChange = timeBankChange;
        this.timeBankIntervals = timeBankIntervals;
        this.scheduledActivities = scheduledActivities;
        this.timeBankCTADistributions = timeBankCTADistributions;
    }

    public TimeBankVisualViewDTO() {
    }

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

    public List<CTADistributionDTO> getTimeBankCTADistributions() {
        return timeBankCTADistributions;
    }

    public void setTimeBankCTADistributions(List<CTADistributionDTO> timeBankCTADistributions) {
        this.timeBankCTADistributions = timeBankCTADistributions;
    }
}
