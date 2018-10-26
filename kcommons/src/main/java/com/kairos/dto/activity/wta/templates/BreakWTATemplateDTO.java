package com.kairos.dto.activity.wta.templates;
/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;

import java.util.Set;

public class BreakWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private short breakGapMinutes;
    private Set<BreakAvailabilitySettings> breakAvailability;

    public BreakWTATemplateDTO(short breakGapMinutes, Set<BreakAvailabilitySettings> breakAvailability) {
        this.breakGapMinutes = breakGapMinutes;
        this.breakAvailability = breakAvailability;
    }

    public BreakWTATemplateDTO(String name, String description, short breakGapMinutes, Set<BreakAvailabilitySettings> breakAvailability) {
        super(name, description);
        this.breakGapMinutes = breakGapMinutes;
        this.breakAvailability = breakAvailability;
    }

    public BreakWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.WTA_FOR_BREAKS_IN_SHIFT;

    }

    public short getBreakGapMinutes() {
        return breakGapMinutes;
    }

    public void setBreakGapMinutes(short breakGapMinutes) {
        this.breakGapMinutes = breakGapMinutes;
    }

    public Set<BreakAvailabilitySettings> getBreakAvailability() {
        return breakAvailability;
    }

    public void setBreakAvailability(Set<BreakAvailabilitySettings> breakAvailability) {
        this.breakAvailability = breakAvailability;
    }
}
