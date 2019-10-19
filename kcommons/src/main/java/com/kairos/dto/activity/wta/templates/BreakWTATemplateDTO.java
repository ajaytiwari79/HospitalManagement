package com.kairos.dto.activity.wta.templates;
/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.Set;

@Getter
@Setter
public class BreakWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private short breakGapMinutes;
    @Valid
    private Set<BreakAvailabilitySettings> breakAvailability;

}
