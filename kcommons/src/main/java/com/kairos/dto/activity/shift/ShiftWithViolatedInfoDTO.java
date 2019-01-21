package com.kairos.dto.activity.shift;

import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ShiftWithViolatedInfoDTO {
    @Valid
    private List<ShiftDTO> shifts;
    private ViolatedRulesDTO violatedRules = new ViolatedRulesDTO();

    public ShiftWithViolatedInfoDTO() {
    }

    public ShiftWithViolatedInfoDTO(List<ShiftDTO> shifts, ViolatedRulesDTO violatedRules) {
        this.shifts = shifts;
        this.violatedRules = violatedRules;
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
