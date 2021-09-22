package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import static com.kairos.commons.utils.DateUtils.asDate;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    @Autowired
    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private boolean restingTimeAllowed;
    private int restingTime;
    private float recommendedValue;
    private transient DateTimeInterval interval;

    public DaysOffInPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.DAYS_OFF_IN_PERIOD;
    }

}
