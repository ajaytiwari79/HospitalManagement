package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenShiftCancelProcess {
    private boolean shiftAssignedToFictiveEmployee;
    private boolean underStaffingPresentForActivity;
    private boolean overStaffingPresentForActivity;
    private boolean sickPersonCallsUnSick;
    private Integer shortestWorkingTime;

    public OpenShiftCancelProcess() {
        //Default Constructor
    }

    public OpenShiftCancelProcess(boolean shiftAssignedToFictiveEmployee, boolean underStaffingPresentForActivity, boolean overStaffingPresentForActivity,
                                  boolean sickPersonCallsUnSick, Integer shortestWorkingTime) {
        this.shiftAssignedToFictiveEmployee = shiftAssignedToFictiveEmployee;
        this.underStaffingPresentForActivity = underStaffingPresentForActivity;
        this.overStaffingPresentForActivity = overStaffingPresentForActivity;
        this.sickPersonCallsUnSick = sickPersonCallsUnSick;
        this.shortestWorkingTime = shortestWorkingTime;
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

    public Integer getShortestWorkingTime() {
        return shortestWorkingTime;
    }

    public void setShortestWorkingTime(Integer shortestWorkingTime) {
        this.shortestWorkingTime = shortestWorkingTime;
    }

    public boolean isSickPersonCallsUnSick() {
        return sickPersonCallsUnSick;
    }

    public void setSickPersonCallsUnSick(boolean sickPersonCallsUnSick) {
        this.sickPersonCallsUnSick = sickPersonCallsUnSick;
    }
}
