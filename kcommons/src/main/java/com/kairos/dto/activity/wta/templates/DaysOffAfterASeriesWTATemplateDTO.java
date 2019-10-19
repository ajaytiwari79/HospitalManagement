package com.kairos.dto.activity.wta.templates;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffAfterASeriesWTATemplateDTO extends WTABaseRuleTemplateDTO {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;

    public DaysOffAfterASeriesWTATemplateDTO() {
        this.wtaTemplateType=WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }
}
