package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.ShiftLengthAndAverageSetting;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftLengthWTATemplate extends WTABaseRuleTemplate {

    private long timeLimit;
    private List<BigInteger> dayTypeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private ShiftLengthAndAverageSetting shiftLengthAndAverageSetting = ShiftLengthAndAverageSetting.DIFFERENCE_BETWEEN_START_END_TIME;
    private transient DateTimeInterval interval;

    public ShiftLengthWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }


}