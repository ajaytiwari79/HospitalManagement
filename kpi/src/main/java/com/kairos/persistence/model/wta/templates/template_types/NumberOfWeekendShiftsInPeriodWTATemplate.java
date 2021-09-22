package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalTime;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NumberOfWeekendShiftsInPeriodWTATemplate extends WTABaseRuleTemplate {


    private String fromDayOfWeek; //(day of week)
    private LocalTime fromTime;
    private LocalTime toTime;
    private String toDayOfWeek;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;
    private transient DateTimeInterval interval;
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

    public NumberOfWeekendShiftsInPeriodWTATemplate() {
        this.wtaTemplateType = WTATemplateType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;

    }



}
