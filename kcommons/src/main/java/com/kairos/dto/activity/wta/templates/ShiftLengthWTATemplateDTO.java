package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftLengthWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private long timeLimit;

    private List<Long> dayTypeIds = new ArrayList<>();
    protected List<PartOfDay> partOfDays = new ArrayList<>();
    protected float recommendedValue;
    private MinMaxSetting minMaxSetting;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private ShiftLengthAndAverageSetting shiftLengthAndAverageSetting;

    public ShiftLengthWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;;
    }

}