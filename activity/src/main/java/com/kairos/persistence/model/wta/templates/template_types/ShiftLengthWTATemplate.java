package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

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
    private List<Long> dayTypeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private ShiftLengthAndAverageSetting shiftLengthAndAverageSetting = ShiftLengthAndAverageSetting.DIFFERENCE_BETWEEN_START_END_TIME;

    public ShiftLengthWTATemplate(String name, String description, long timeLimit) {
        super(name, description);
        this.timeLimit = timeLimit;
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }


    public ShiftLengthWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(), this.phaseTemplateValues)) {
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrapperMap(), infoWrapper.getShift());
            if (timeInterval != null) {
                boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds()));
                if (isValidShift && isValidForDay(dayTypeIds, infoWrapper)) {
                    ShiftWithActivityDTO shift = infoWrapper.getShift();
                    Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, phaseTemplateValues, this);
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], getValueAccordingShiftLengthAndAverageSetting(shiftLengthAndAverageSetting, shift));
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                            limitAndCounter[2],DurationType.HOURS,getHoursByMinutes(limitAndCounter[0],this.name));

                }
            }
        }
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ShiftLengthWTATemplate shiftLengthWTATemplate = (ShiftLengthWTATemplate) wtaBaseRuleTemplate;
        return (this != shiftLengthWTATemplate) && !(timeLimit == shiftLengthWTATemplate.timeLimit &&
                Float.compare(shiftLengthWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(dayTypeIds, shiftLengthWTATemplate.dayTypeIds) &&
                Objects.equals(timeTypeIds, shiftLengthWTATemplate.timeTypeIds) &&
                Objects.equals(partOfDays, shiftLengthWTATemplate.partOfDays) &&
                minMaxSetting == shiftLengthWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,shiftLengthWTATemplate.phaseTemplateValues));
    }
}