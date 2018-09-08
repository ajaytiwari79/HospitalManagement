package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenShiftCancelProcess {
    private boolean shiftAssignedToFictiveEmployee;
    private boolean underStaffingPresentForActivity;
    private boolean overStaffingPresentForActivity;
    private boolean sickPersonCallsUnSick;
    private boolean lessUnderStaffing; //if underStaffing is less than shortest working time

    public OpenShiftCancelProcess() {
        //Default Constructor
    }

    public OpenShiftCancelProcess(boolean shiftAssignedToFictiveEmployee, boolean underStaffingPresentForActivity, boolean overStaffingPresentForActivity,
                                  boolean sickPersonCallsUnSick, boolean lessUnderStaffing) {
        this.shiftAssignedToFictiveEmployee = shiftAssignedToFictiveEmployee;
        this.underStaffingPresentForActivity = underStaffingPresentForActivity;
        this.overStaffingPresentForActivity = overStaffingPresentForActivity;
        this.sickPersonCallsUnSick = sickPersonCallsUnSick;
        this.lessUnderStaffing = lessUnderStaffing;
    }

    public boolean isShiftAssignedToFictiveEmployee() {
        return shiftAssignedToFictiveEmployee;
    }

    public void setShiftAssignedToFictiveEmployee(boolean shiftAssignedToFictiveEmployee) {
        this.shiftAssignedToFictiveEmployee = shiftAssignedToFictiveEmployee;
    }

    public boolean isUnderStaffingPresentForActivity() {
        return underStaffingPresentForActivity;
    }

    public void setUnderStaffingPresentForActivity(boolean underStaffingPresentForActivity) {
        this.underStaffingPresentForActivity = underStaffingPresentForActivity;
    }

    public boolean isOverStaffingPresentForActivity() {
        return overStaffingPresentForActivity;
    }

    public void setOverStaffingPresentForActivity(boolean overStaffingPresentForActivity) {
        this.overStaffingPresentForActivity = overStaffingPresentForActivity;
    }

    public boolean getLessUnderStaffing() {
        return lessUnderStaffing;
    }

    public void setLessUnderStaffing(boolean lessUnderStaffing) {
        this.lessUnderStaffing = lessUnderStaffing;
    }

    public boolean isSickPersonCallsUnSick() {
        return sickPersonCallsUnSick;
    }

    public void setSickPersonCallsUnSick(boolean sickPersonCallsUnSick) {
        this.sickPersonCallsUnSick = sickPersonCallsUnSick;
    }
}
