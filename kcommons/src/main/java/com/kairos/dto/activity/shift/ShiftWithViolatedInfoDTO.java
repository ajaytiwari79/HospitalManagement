package com.kairos.dto.activity.shift;

import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ShiftWithViolatedInfoDTO {
    private List<ShiftDTO> shifts;
    private ViolatedRulesDTO violatedRules;


    public ShiftWithViolatedInfoDTO() {
    }

    public ShiftWithViolatedInfoDTO(List<ShiftDTO> shifts, ViolatedRulesDTO violatedRules) {
        this.shifts = shifts;
        this.violatedRules = violatedRules;
    }

    public List<ShiftDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }

    public ViolatedRulesDTO getViolatedRules() {
        return violatedRules;
    }

    public void setViolatedRules(ViolatedRulesDTO violatedRules) {
        this.violatedRules = violatedRules;
    }
}
