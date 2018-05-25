package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.PhaseTemplateValue;
import com.kairos.activity.persistence.model.activity.tabs.RulesActivityTab;

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
    private Integer earliestStartTime;
    private Integer latestStartTime;
    private Integer shortestTime;
    private Integer longestTime;
    private boolean eligibleForCopy;


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
                approvalAllowed,earliestStartTime,latestStartTime, shortestTime, longestTime, eligibleForCopy);


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

    public Integer getEarliestStartTime() {
        return earliestStartTime;
    }

    public void setEarliestStartTime(Integer earliestStartTime) {
        this.earliestStartTime = earliestStartTime;
    }

    public Integer getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(Integer latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public Integer getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(Integer shortestTime) {
        this.shortestTime = shortestTime;
    }

    public Integer getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(Integer longestTime) {
        this.longestTime = longestTime;
    }
    public boolean isEligibleForCopy() {
        return eligibleForCopy;
    }

    public void setEligibleForCopy(boolean eligibleForCopy) {
        this.eligibleForCopy = eligibleForCopy;
        }
}
