package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftsInIntervalWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private List<String> balanceType;//multiple check boxes
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private List<BigInteger> timeTypeIds;
    private List<BigInteger> plannedTimeIds;
    protected List<PartOfDay> partOfDays;
    protected float recommendedValue;
    private MinMaxSetting minMaxSetting;

    public ShiftsInIntervalWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.NUMBER_OF_SHIFTS_IN_INTERVAL;
    }
}
