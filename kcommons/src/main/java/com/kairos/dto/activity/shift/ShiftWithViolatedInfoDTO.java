package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftWithViolatedInfoDTO {
    @Valid
    @NotEmpty
    private List<ShiftDTO> shifts;
    private ViolatedRulesDTO violatedRules = new ViolatedRulesDTO();
    private String actionPerformed;

    public ShiftWithViolatedInfoDTO(ViolatedRulesDTO violatedRules) {
        this.violatedRules = violatedRules;
    }

    public ShiftWithViolatedInfoDTO(@Valid List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }

    public ShiftWithViolatedInfoDTO(List<ShiftDTO> shifts, ViolatedRulesDTO violatedRules){
        this.shifts = shifts;
        this.violatedRules = violatedRules;
    }
}
