package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE4
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
public class ConsecutiveRestPartOfDayWTATemplate extends WTABaseRuleTemplate {

    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);

    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

    public ConsecutiveRestPartOfDayWTATemplate() {
        wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
    }

}
