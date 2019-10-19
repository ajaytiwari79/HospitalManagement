package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DurationBetweenShiftsWTATemplateDTO extends WTABaseRuleTemplateDTO {


    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting;

    public DurationBetweenShiftsWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }
    }