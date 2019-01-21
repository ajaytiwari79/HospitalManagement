package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffExcludeFilter {
    private Integer numberOfShiftAssigned;
    private Integer numberOfPendingRequest;
    private Integer unitExperienceInWeek;
    private Integer minTimeBank; // In Minutes
    private Integer minRestingTimeBeforeShiftStart; // In Minutes
    private Integer minRestingTimeAfterShiftEnd; // In Minutes
    private Integer maxPlannedTime; // In Minutes
    private Integer maxDeltaWeeklyTimeBankPerWeek; // In Minutes
    private boolean personalEntriesFoundFromPrivateCalender;
    private Integer lastWorkingDaysInUnit;
    private Integer lastWorkingDaysWithActivity;
    private Integer minRemainingTimeLeftInActivityPlanningPeriod; //In Minutes
    private boolean negativeAvailabilityInCalender;
    private boolean veto;
    private boolean stopBricks;


    public StaffExcludeFilter() {
        //Default Constructor
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

    public Integer getMinTimeBank() {
        return minTimeBank;
    }

    public void setMinTimeBank(Integer minTimeBank) {
        this.minTimeBank = minTimeBank;
    }

    public Integer getMinRestingTimeBeforeShiftStart() {
        return minRestingTimeBeforeShiftStart;
    }

    public void setMinRestingTimeBeforeShiftStart(Integer minRestingTimeBeforeShiftStart) {
        this.minRestingTimeBeforeShiftStart = minRestingTimeBeforeShiftStart;
    }

    public Integer getMinRestingTimeAfterShiftEnd() {
        return minRestingTimeAfterShiftEnd;
    }

    public void setMinRestingTimeAfterShiftEnd(Integer minRestingTimeAfterShiftEnd) {
        this.minRestingTimeAfterShiftEnd = minRestingTimeAfterShiftEnd;
    }

    public Integer getMaxPlannedTime() {
        return maxPlannedTime;
    }

    public void setMaxPlannedTime(Integer maxPlannedTime) {
        this.maxPlannedTime = maxPlannedTime;
    }

    public Integer getMaxDeltaWeeklyTimeBankPerWeek() {
        return maxDeltaWeeklyTimeBankPerWeek;
    }

    public void setMaxDeltaWeeklyTimeBankPerWeek(Integer maxDeltaWeeklyTimeBankPerWeek) {
        this.maxDeltaWeeklyTimeBankPerWeek = maxDeltaWeeklyTimeBankPerWeek;
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

    public Integer getMinRemainingTimeLeftInActivityPlanningPeriod() {
        return minRemainingTimeLeftInActivityPlanningPeriod;
    }

    public void setMinRemainingTimeLeftInActivityPlanningPeriod(Integer minRemainingTimeLeftInActivityPlanningPeriod) {
        this.minRemainingTimeLeftInActivityPlanningPeriod = minRemainingTimeLeftInActivityPlanningPeriod;
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
