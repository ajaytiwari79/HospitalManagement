package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.open_shift.DurationField;
import com.kairos.persistence.model.activity.tabs.PhaseTemplateValue;
import com.kairos.persistence.model.activity.tabs.RulesActivityTab;

import javax.validation.constraints.AssertTrue;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RulesActivityTabDTO {

    private Long activityId;
    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean eligibleAgainstTimeRules;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    private List<Long> dayTypes;
    private List<PhaseTemplateValue> eligibleForSchedules;
    private boolean eligibleForStaffingLevel;
    private boolean eligibleForPresence;
    private boolean eligibleForAbsence;
    private boolean breakAllowed = false;
    private boolean approvalAllowed = false;

    // in Minutes
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private int shortestTime;
    private int longestTime;
    private boolean eligibleForCopy;

    private DurationField plannedTimeInAdvance;
    private DurationField approvalTimeInAdvance;
    private Float approvalPercentage;
    private LocalTime maximumEndTime;



    public boolean isEligibleForStaffingLevel() {
        return eligibleForStaffingLevel;
    }

    public void setEligibleForStaffingLevel(boolean eligibleForStaffingLevel) {
        this.eligibleForStaffingLevel = eligibleForStaffingLevel;
    }

    public boolean isEligibleForPresence() {
        return eligibleForPresence;
    }

    public void setEligibleForPresence(boolean eligibleForPresence) {
        this.eligibleForPresence = eligibleForPresence;
    }

    public boolean isEligibleForAbsence() {
        return eligibleForAbsence;
    }

    public void setEligibleForAbsence(boolean eligibleForAbsence) {
        this.eligibleForAbsence = eligibleForAbsence;
    }





    public RulesActivityTab buildRulesActivityTab() {
        RulesActivityTab rulesActivityTab = new RulesActivityTab( eligibleForFinalSchedule, eligibleForDraftSchedule, eligibleForRequest,

                 eligibleAgainstTimeRules,  lockLengthPresent, eligibleToBeForced,dayTypes,this.eligibleForSchedules,eligibleForStaffingLevel,eligibleForPresence,eligibleForAbsence, breakAllowed,
                approvalAllowed,earliestStartTime,latestStartTime, shortestTime, longestTime, eligibleForCopy,plannedTimeInAdvance,approvalTimeInAdvance,approvalPercentage,maximumEndTime);


        return rulesActivityTab;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public boolean isEligibleForFinalSchedule() {
        return eligibleForFinalSchedule;
    }

    public void setEligibleForFinalSchedule(boolean eligibleForFinalSchedule) {
        this.eligibleForFinalSchedule = eligibleForFinalSchedule;
    }

    public boolean isEligibleForDraftSchedule() {
        return eligibleForDraftSchedule;
    }

    public void setEligibleForDraftSchedule(boolean eligibleForDraftSchedule) {
        this.eligibleForDraftSchedule = eligibleForDraftSchedule;
    }

    public boolean isEligibleForRequest() {
        return eligibleForRequest;
    }

    public void setEligibleForRequest(boolean eligibleForRequest) {
        this.eligibleForRequest = eligibleForRequest;
    }



    public boolean isEligibleAgainstTimeRules() {
        return eligibleAgainstTimeRules;
    }

    public void setEligibleAgainstTimeRules(boolean eligibleAgainstTimeRules) {
        this.eligibleAgainstTimeRules = eligibleAgainstTimeRules;
    }



    public boolean isLockLengthPresent() {
        return lockLengthPresent;
    }

    public void setLockLengthPresent(boolean lockLengthPresent) {
        this.lockLengthPresent = lockLengthPresent;
    }

    public boolean isEligibleToBeForced() {
        return eligibleToBeForced;
    }

    public void setEligibleToBeForced(boolean eligibleToBeForced) {
        this.eligibleToBeForced = eligibleToBeForced;
    }

    public List<Long> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<Long> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public List<PhaseTemplateValue> getEligibleForSchedules() {
        return eligibleForSchedules;
    }

    public void setEligibleForSchedules(List<PhaseTemplateValue> eligibleForSchedules) {
        this.eligibleForSchedules = eligibleForSchedules;
    }

    public boolean isBreakAllowed() {
        return breakAllowed;
    }

    public void setBreakAllowed(boolean breakAllowed) {
        this.breakAllowed = breakAllowed;
    }

    public boolean isApprovalAllowed() {
        return approvalAllowed;
    }

    public void setApprovalAllowed(boolean approvalAllowed) {
        this.approvalAllowed = approvalAllowed;
    }

    public LocalTime getEarliestStartTime() {
        return earliestStartTime;
    }

    public void setEarliestStartTime(LocalTime earliestStartTime) {
        this.earliestStartTime = earliestStartTime;
    }

    public LocalTime getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(LocalTime latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public int getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(int shortestTime) {
        this.shortestTime = shortestTime;
    }

    public int getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(int longestTime) {
        this.longestTime = longestTime;
    }

    public boolean isEligibleForCopy() {
        return eligibleForCopy;
    }

    public void setEligibleForCopy(boolean eligibleForCopy) {
        this.eligibleForCopy = eligibleForCopy;
        }

    public DurationField getPlannedTimeInAdvance() {
        return plannedTimeInAdvance;
    }

    public void setPlannedTimeInAdvance(DurationField plannedTimeInAdvance) {
        this.plannedTimeInAdvance = plannedTimeInAdvance;
    }

    public DurationField getApprovalTimeInAdvance() {
        return approvalTimeInAdvance;
    }

    public void setApprovalTimeInAdvance(DurationField approvalTimeInAdvance) {
        this.approvalTimeInAdvance = approvalTimeInAdvance;
    }

    public Float getApprovalPercentage() {
        return approvalPercentage;
    }

    public void setApprovalPercentage(Float approvalPercentage) {
        this.approvalPercentage = approvalPercentage;
    }

    public LocalTime getMaximumEndTime() {
        return maximumEndTime;
    }

    public void setMaximumEndTime(LocalTime maximumEndTime) {
        this.maximumEndTime = maximumEndTime;
    }

    @AssertTrue(message = "Latest Start time can't before earliest Start time")
    public boolean isValid() {
        return latestStartTime.isAfter(earliestStartTime) && earliestStartTime.plusMinutes(longestTime).isBefore(maximumEndTime);
    }
}
