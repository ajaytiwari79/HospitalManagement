package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 23/8/17.
 */
public class RulesActivityTab implements Serializable{


    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean eligibleAgainstTimeRules;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    private List<Long> dayTypes= new ArrayList<>();
    private List<PhaseTemplateValue> eligibleForSchedules;
    private boolean eligibleForStaffingLevel;
    private boolean eligibleForPresence;
    private boolean eligibleForAbsence;
    private boolean breakAllowed = false;
    private boolean approvalAllowed = false;

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

    public RulesActivityTab() {
    }

    public RulesActivityTab(boolean eligibleForFinalSchedule, boolean eligibleForDraftSchedule, boolean eligibleForRequest, boolean eligibleForStaffingLevel, boolean eligibleForPresence, boolean eligibleAgainstTimeRules, boolean eligibleForAbsence, boolean lockLengthPresent, boolean eligibleToBeForced, List<Long> dayTypes, List<PhaseTemplateValue> eligibleForSchedules) {
        this.eligibleForFinalSchedule = eligibleForFinalSchedule;
        this.eligibleForDraftSchedule = eligibleForDraftSchedule;
        this.eligibleForRequest = eligibleForRequest;
        this.eligibleAgainstTimeRules = eligibleAgainstTimeRules;
        this.lockLengthPresent = lockLengthPresent;
        this.eligibleToBeForced = eligibleToBeForced;
        this.eligibleForStaffingLevel=eligibleForStaffingLevel;
        this.eligibleForPresence=eligibleForPresence;
        this.eligibleForAbsence=eligibleForAbsence;
        this.dayTypes=dayTypes;
        this.eligibleForSchedules = eligibleForSchedules;
    }


    //for time care
    public RulesActivityTab(boolean eligibleForWholeDay,boolean eligibleAgainstTimeRules) {
        this.eligibleAgainstTimeRules = eligibleAgainstTimeRules;

    }

    public RulesActivityTab(boolean eligibleForFinalSchedule, boolean eligibleForDraftSchedule, boolean eligibleForRequest,  boolean eligibleAgainstTimeRules,  boolean lockLengthPresent, boolean eligibleToBeForced,
                            List<Long> dayTypes, List<PhaseTemplateValue> eligibleForSchedules,boolean eligibleForStaffingLevel, boolean eligibleForPresence,boolean eligibleForAbsence, boolean breakAllowed, boolean approvalAllowed) {
        this.eligibleForFinalSchedule = eligibleForFinalSchedule;
        this.eligibleForDraftSchedule = eligibleForDraftSchedule;
        this.eligibleForRequest = eligibleForRequest;
        this.eligibleAgainstTimeRules = eligibleAgainstTimeRules;
        this.lockLengthPresent = lockLengthPresent;
        this.eligibleToBeForced = eligibleToBeForced;
        this.dayTypes = dayTypes;
        this.eligibleForSchedules = eligibleForSchedules;
        this.eligibleForStaffingLevel=eligibleForStaffingLevel;
        this.eligibleForPresence=eligibleForPresence;
        this.eligibleForAbsence=eligibleForAbsence;
        this.breakAllowed = breakAllowed;
        this.approvalAllowed = approvalAllowed;
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
}
