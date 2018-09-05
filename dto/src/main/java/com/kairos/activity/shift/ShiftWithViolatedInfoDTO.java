package com.kairos.activity.shift;

import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ShiftWithViolatedInfoDTO {
    private List<ShiftQueryResult> shifts;
    private ViolatedRulesDTO violatedRules;


    public ShiftWithViolatedInfoDTO() {
    }

    public ShiftWithViolatedInfoDTO(List<ShiftQueryResult> shifts, ViolatedRulesDTO violatedRules) {
        this.shifts = shifts;
        this.violatedRules = violatedRules;
    }

    public List<ShiftQueryResult> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftQueryResult> shifts) {
        this.shifts = shifts;
    }

    public ViolatedRulesDTO getViolatedRules() {
        return violatedRules;
    }

    public void setViolatedRules(ViolatedRulesDTO violatedRules) {
        this.violatedRules = violatedRules;
    }
}
