package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffExcludeFilter {
    private boolean blockedForOpenShift;
    private Integer numberOfShiftAssigned;
    private Integer numberOfPendingRequest;
    private Integer unitExperienceInWeek;
    private Integer minimumTimeBankInHours;
    private Integer minimumRestingTimeBeforeShiftStartInMinutes;
    private Integer minimumRestingTimeAfterShiftEndInMinutes;
    private Integer maximumPlannedHours;
    private Integer maximumDeltaWeeklyTimeBankPerWeekInMinutes;
    private boolean personalEntriesFoundFromPrivateCalender;
    private Integer lastWorkingDaysInUnit;
    private Integer lastWorkingDaysWithActivity;
    private Integer minimumRemainingHoursInActivityPlanningPeriod;
    private boolean negativeAvailabilityInCalender;
    private boolean veto;
    private boolean stopBricks;

    public StaffExcludeFilter() {
        //Default Constructor
    }

    public StaffExcludeFilter(boolean blockedForOpenShift, Integer numberOfShiftAssigned, Integer numberOfPendingRequest, Integer unitExperienceInWeek, Integer minimumTimeBankInHours, Integer minimumRestingTimeBeforeShiftStartInMinutes,
                              Integer minimumRestingTimeAfterShiftEndInMinutes, Integer maximumPlannedHours, Integer maximumDeltaWeeklyTimeBankPerWeekInMinutes, boolean personalEntriesFoundFromPrivateCalender, Integer lastWorkingDaysInUnit,
                              Integer lastWorkingDaysWithActivity, Integer minimumRemainingHoursInActivityPlanningPeriod, boolean negativeAvailabilityInCalender, boolean veto, boolean stopBricks) {
        this.blockedForOpenShift = blockedForOpenShift;
        this.numberOfShiftAssigned = numberOfShiftAssigned;
        this.numberOfPendingRequest = numberOfPendingRequest;
        this.unitExperienceInWeek = unitExperienceInWeek;
        this.minimumTimeBankInHours = minimumTimeBankInHours;
        this.minimumRestingTimeBeforeShiftStartInMinutes = minimumRestingTimeBeforeShiftStartInMinutes;
        this.minimumRestingTimeAfterShiftEndInMinutes = minimumRestingTimeAfterShiftEndInMinutes;
        this.maximumPlannedHours = maximumPlannedHours;
        this.maximumDeltaWeeklyTimeBankPerWeekInMinutes = maximumDeltaWeeklyTimeBankPerWeekInMinutes;
        this.personalEntriesFoundFromPrivateCalender = personalEntriesFoundFromPrivateCalender;
        this.lastWorkingDaysInUnit = lastWorkingDaysInUnit;
        this.lastWorkingDaysWithActivity = lastWorkingDaysWithActivity;
        this.minimumRemainingHoursInActivityPlanningPeriod = minimumRemainingHoursInActivityPlanningPeriod;
        this.negativeAvailabilityInCalender = negativeAvailabilityInCalender;
        this.veto = veto;
        this.stopBricks = stopBricks;
    }

    public boolean isBlockedForOpenShift() {
        return blockedForOpenShift;
    }

    public void setBlockedForOpenShift(boolean blockedForOpenShift) {
        this.blockedForOpenShift = blockedForOpenShift;
    }

    public Integer getNumberOfShiftAssigned() {
        return numberOfShiftAssigned;
    }

    public void setNumberOfShiftAssigned(Integer numberOfShiftAssigned) {
        this.numberOfShiftAssigned = numberOfShiftAssigned;
    }

    public Integer getNumberOfPendingRequest() {
        return numberOfPendingRequest;
    }

    public void setNumberOfPendingRequest(Integer numberOfPendingRequest) {
        this.numberOfPendingRequest = numberOfPendingRequest;
    }

    public Integer getUnitExperienceInWeek() {
        return unitExperienceInWeek;
    }

    public void setUnitExperienceInWeek(Integer unitExperienceInWeek) {
        this.unitExperienceInWeek = unitExperienceInWeek;
    }

    public Integer getMinimumTimeBankInHours() {
        return minimumTimeBankInHours;
    }

    public void setMinimumTimeBankInHours(Integer minimumTimeBankInHours) {
        this.minimumTimeBankInHours = minimumTimeBankInHours;
    }

    public Integer getMinimumRestingTimeBeforeShiftStartInMinutes() {
        return minimumRestingTimeBeforeShiftStartInMinutes;
    }

    public void setMinimumRestingTimeBeforeShiftStartInMinutes(Integer minimumRestingTimeBeforeShiftStartInMinutes) {
        this.minimumRestingTimeBeforeShiftStartInMinutes = minimumRestingTimeBeforeShiftStartInMinutes;
    }

    public Integer getMinimumRestingTimeAfterShiftEndInMinutes() {
        return minimumRestingTimeAfterShiftEndInMinutes;
    }

    public void setMinimumRestingTimeAfterShiftEndInMinutes(Integer minimumRestingTimeAfterShiftEndInMinutes) {
        this.minimumRestingTimeAfterShiftEndInMinutes = minimumRestingTimeAfterShiftEndInMinutes;
    }

    public Integer getMaximumPlannedHours() {
        return maximumPlannedHours;
    }

    public void setMaximumPlannedHours(Integer maximumPlannedHours) {
        this.maximumPlannedHours = maximumPlannedHours;
    }

    public Integer getMaximumDeltaWeeklyTimeBankPerWeekInMinutes() {
        return maximumDeltaWeeklyTimeBankPerWeekInMinutes;
    }

    public void setMaximumDeltaWeeklyTimeBankPerWeekInMinutes(Integer maximumDeltaWeeklyTimeBankPerWeekInMinutes) {
        this.maximumDeltaWeeklyTimeBankPerWeekInMinutes = maximumDeltaWeeklyTimeBankPerWeekInMinutes;
    }

    public boolean isPersonalEntriesFoundFromPrivateCalender() {
        return personalEntriesFoundFromPrivateCalender;
    }

    public void setPersonalEntriesFoundFromPrivateCalender(boolean personalEntriesFoundFromPrivateCalender) {
        this.personalEntriesFoundFromPrivateCalender = personalEntriesFoundFromPrivateCalender;
    }

    public Integer getLastWorkingDaysInUnit() {
        return lastWorkingDaysInUnit;
    }

    public void setLastWorkingDaysInUnit(Integer lastWorkingDaysInUnit) {
        this.lastWorkingDaysInUnit = lastWorkingDaysInUnit;
    }

    public Integer getLastWorkingDaysWithActivity() {
        return lastWorkingDaysWithActivity;
    }

    public void setLastWorkingDaysWithActivity(Integer lastWorkingDaysWithActivity) {
        this.lastWorkingDaysWithActivity = lastWorkingDaysWithActivity;
    }

    public Integer getMinimumRemainingHoursInActivityPlanningPeriod() {
        return minimumRemainingHoursInActivityPlanningPeriod;
    }

    public void setMinimumRemainingHoursInActivityPlanningPeriod(Integer minimumRemainingHoursInActivityPlanningPeriod) {
        this.minimumRemainingHoursInActivityPlanningPeriod = minimumRemainingHoursInActivityPlanningPeriod;
    }

    public boolean isNegativeAvailabilityInCalender() {
        return negativeAvailabilityInCalender;
    }

    public void setNegativeAvailabilityInCalender(boolean negativeAvailabilityInCalender) {
        this.negativeAvailabilityInCalender = negativeAvailabilityInCalender;
    }

    public boolean isVeto() {
        return veto;
    }

    public void setVeto(boolean veto) {
        this.veto = veto;
    }

    public boolean isStopBricks() {
        return stopBricks;
    }

    public void setStopBricks(boolean stopBricks) {
        this.stopBricks = stopBricks;
    }
}
