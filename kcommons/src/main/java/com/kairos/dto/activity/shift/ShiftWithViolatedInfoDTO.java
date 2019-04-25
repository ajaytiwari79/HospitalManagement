package com.kairos.dto.activity.shift;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ShiftWithViolatedInfoDTO {
    @Valid
    private List<ShiftDTO> shifts;
    private ViolatedRulesDTO violatedRules = new ViolatedRulesDTO();

    public ShiftWithViolatedInfoDTO() {
        //Default Constructor
    }


    public ShiftWithViolatedInfoDTO(ViolatedRulesDTO violatedRules) {
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
