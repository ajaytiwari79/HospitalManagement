package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class OpenShiftCancelProcess {
    private boolean shiftAssignedToFictiveEmployee;
    private boolean underStaffingPresentForActivity;
    private boolean overStaffingPresentForActivity;
    private boolean sickPersonCallsUnSick;
    private boolean lessUnderStaffing; //if underStaffing is less than shortest working time


    public OpenShiftCancelProcess(boolean shiftAssignedToFictiveEmployee, boolean underStaffingPresentForActivity, boolean overStaffingPresentForActivity,
                                  boolean sickPersonCallsUnSick, boolean lessUnderStaffing) {
        this.shiftAssignedToFictiveEmployee = shiftAssignedToFictiveEmployee;
        this.underStaffingPresentForActivity = underStaffingPresentForActivity;
        this.overStaffingPresentForActivity = overStaffingPresentForActivity;
        this.sickPersonCallsUnSick = sickPersonCallsUnSick;
        this.lessUnderStaffing = lessUnderStaffing;
    }

}
