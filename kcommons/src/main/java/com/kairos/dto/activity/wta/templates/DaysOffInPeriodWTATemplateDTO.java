package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffInPeriodWTATemplateDTO extends WTABaseRuleTemplateDTO {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private MinMaxSetting minMaxSetting;
    private boolean restingTimeAllowed;
    private int restingTime;
    private float recommendedValue;

    public DaysOffInPeriodWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.DAYS_OFF_IN_PERIOD;
    }
}
