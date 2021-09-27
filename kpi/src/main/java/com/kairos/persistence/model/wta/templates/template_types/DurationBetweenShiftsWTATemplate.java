package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private transient DateTimeInterval interval;



    public DurationBetweenShiftsWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }

}
