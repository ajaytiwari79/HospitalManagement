package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.dto.activity.activity.activity_tabs.PQLSettings;
import com.kairos.dto.activity.open_shift.DurationField;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawanmandhan on 23/8/17.
 */
public class RulesActivityTab implements Serializable{


    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    private List<Long> dayTypes= new ArrayList<>();
    private List<PhaseTemplateValue> eligibleForSchedules;
    private boolean eligibleForStaffingLevel;

    private boolean breakAllowed = false;
    private boolean approvalAllowed = false;
    private LocalDate cutOffStartFrom;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private Integer cutOffdayValue;
    private List<CutOffInterval> cutOffIntervals;
    // in Minutes
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private Short shortestTime;
    private Short longestTime;
    private boolean eligibleForCopy;
    private DurationField plannedTimeInAdvance;
    private LocalTime maximumEndTime;// shift can't be extend this time
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --   -128 to 127
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.     -128 to 127
    private PQLSettings pqlSettings;

    public RulesActivityTab() {
        //Default Constructor
    }

    public RulesActivityTab(List<PhaseTemplateValue> eligibleForSchedules) {
        this.eligibleForSchedules = eligibleForSchedules;
    }

    public RulesActivityTab(boolean eligibleForFinalSchedule, boolean eligibleForDraftSchedule, boolean eligibleForRequest, boolean lockLengthPresent, boolean eligibleToBeForced,
                            List<Long> dayTypes, List<PhaseTemplateValue> eligibleForSchedules, boolean eligibleForStaffingLevel, boolean breakAllowed, boolean approvalAllowed
            , LocalTime earliestStartTime, LocalTime latestStartTime, Short shortestTime, Short longestTime, boolean eligibleForCopy,DurationField plannedTimeInAdvance,PQLSettings pqlSettings,LocalTime maximumEndTime,Integer cutOffdayValue,CutOffIntervalUnit cutOffIntervalUnit,LocalDate cutOffStartFrom,byte recurrenceDays,byte recurrenceTimes,boolean allowedAutoAbsence) {

        this.eligibleForFinalSchedule = eligibleForFinalSchedule;
        this.eligibleForDraftSchedule = eligibleForDraftSchedule;
        this.eligibleForRequest = eligibleForRequest;
        this.lockLengthPresent = lockLengthPresent;
        this.eligibleToBeForced = eligibleToBeForced;
        this.dayTypes = dayTypes;
        this.eligibleForSchedules = eligibleForSchedules;
        this.eligibleForStaffingLevel=eligibleForStaffingLevel;
        this.breakAllowed = breakAllowed;
        this.approvalAllowed = approvalAllowed;
        this.earliestStartTime=earliestStartTime;
        this.latestStartTime=latestStartTime;
        this.shortestTime = shortestTime;
        this.longestTime = longestTime;
        this.eligibleForCopy=eligibleForCopy;
        this.plannedTimeInAdvance=plannedTimeInAdvance;
        this.pqlSettings=pqlSettings;
        this.maximumEndTime=maximumEndTime;
        this.cutOffdayValue = cutOffdayValue;
        this.cutOffIntervalUnit = cutOffIntervalUnit;
        this.cutOffStartFrom = cutOffStartFrom;
        this.recurrenceDays=recurrenceDays;
        this.recurrenceTimes=recurrenceTimes;
        this.allowedAutoAbsence=allowedAutoAbsence;
    }

    public LocalDate getCutOffStartFrom() {
        return cutOffStartFrom;
    }

    public void setCutOffStartFrom(LocalDate cutOffStartFrom) {
        this.cutOffStartFrom = cutOffStartFrom;
    }

    public CutOffIntervalUnit getCutOffIntervalUnit() {
        return cutOffIntervalUnit;
    }

    public void setCutOffIntervalUnit(CutOffIntervalUnit cutOffIntervalUnit) {
        this.cutOffIntervalUnit = cutOffIntervalUnit;
    }

    public Integer getCutOffdayValue() {
        return cutOffdayValue;
    }

    public void setCutOffdayValue(Integer cutOffdayValue) {
        this.cutOffdayValue = cutOffdayValue;
    }

    public List<CutOffInterval> getCutOffIntervals() {
        return cutOffIntervals;
    }

    public void setCutOffIntervals(List<CutOffInterval> cutOffIntervals) {
        this.cutOffIntervals = cutOffIntervals;
    }

    public boolean isEligibleForStaffingLevel() {
        return eligibleForStaffingLevel;
    }

    public void setEligibleForStaffingLevel(boolean eligibleForStaffingLevel) {
        this.eligibleForStaffingLevel = eligibleForStaffingLevel;
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

    public Short getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(Short shortestTime) {
        this.shortestTime = shortestTime;
    }

    public Short getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(Short longestTime) {
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

    public LocalTime getMaximumEndTime() {
        return maximumEndTime;
    }

    public void setMaximumEndTime(LocalTime maximumEndTime) {
        this.maximumEndTime = maximumEndTime;
    }

    public byte getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(byte recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

    public byte getRecurrenceTimes() {
        return recurrenceTimes;
    }

    public void setRecurrenceTimes(byte recurrenceTimes) {
        this.recurrenceTimes = recurrenceTimes;
    }

    public boolean isAllowedAutoAbsence() {
        return allowedAutoAbsence;
    }

    public void setAllowedAutoAbsence(boolean allowedAutoAbsence) {
        this.allowedAutoAbsence = allowedAutoAbsence;
    }

    public PQLSettings getPqlSettings() {
        return pqlSettings=Optional.ofNullable(pqlSettings).orElse(new PQLSettings());
    }

    public void setPqlSettings(PQLSettings pqlSettings) {
        this.pqlSettings = pqlSettings;
    }
}
