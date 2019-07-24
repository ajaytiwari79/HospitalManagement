package com.kairos.persistence.model.wta.templates.template_types;
/*
 *Created By Pavan on 25/10/18
 *
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.templates.BreakAvailabilitySettings;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class BreakWTATemplate extends WTABaseRuleTemplate {

    private short breakGapMinutes;
    private Set<BreakAvailabilitySettings> breakAvailability;

    public BreakWTATemplate(String name, String description, short breakGapMinutes, Set<BreakAvailabilitySettings> breakAvailability) {
        super(name, description);
        this.breakGapMinutes = breakGapMinutes;
        this.breakAvailability = breakAvailability;
    }

    public BreakWTATemplate() {
        //Default Constructor
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        BreakWTATemplate breakWTATemplate = (BreakWTATemplate)wtaBaseRuleTemplate;
        return (this != breakWTATemplate) && !(breakGapMinutes == breakWTATemplate.breakGapMinutes &&
                Objects.equals(breakAvailability, breakWTATemplate.breakAvailability));
    }

}
