package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
@NoArgsConstructor
public class ShiftWithViolatedInfoDTO {
    @Valid
    private List<ShiftDTO> shifts;
    private ViolatedRulesDTO violatedRules = new ViolatedRulesDTO();

    public ShiftWithViolatedInfoDTO(ViolatedRulesDTO violatedRules) {
        this.violatedRules = violatedRules;
    }




}
