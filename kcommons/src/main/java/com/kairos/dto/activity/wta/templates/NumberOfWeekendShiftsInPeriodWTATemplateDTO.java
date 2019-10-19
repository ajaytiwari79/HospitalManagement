package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalTime;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NumberOfWeekendShiftsInPeriodWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private String fromDayOfWeek;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime fromTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime toTime;
    private String toDayOfWeek;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;


    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

    public NumberOfWeekendShiftsInPeriodWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;
    }


}
