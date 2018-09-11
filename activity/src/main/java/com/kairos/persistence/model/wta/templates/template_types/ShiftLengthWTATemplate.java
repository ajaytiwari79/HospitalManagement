package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.enums.MinMaxSetting;
import com.kairos.enums.PartOfDay;
import com.kairos.enums.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.util.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.util.TimeInterval;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.util.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftLengthWTATemplate extends WTABaseRuleTemplate {

    private long timeLimit;
    private List<Long> dayTypeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public List<PartOfDay> getPartOfDays() {
        return partOfDays;
    }

    public void setPartOfDays(List<PartOfDay> partOfDays) {
        this.partOfDays = partOfDays;
    }

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }


    public ShiftLengthWTATemplate() {
        wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)) {
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrappers(), infoWrapper.getShift());
            if (timeInterval != null) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = filterShifts(Arrays.asList(infoWrapper.getShift()),timeTypeIds,null,null);
                if(!shiftWithActivityDTOS.isEmpty()){
                    ShiftWithActivityDTO shift = shiftWithActivityDTOS.get(0);
                    if (isValidForDay(dayTypeIds, infoWrapper)) {
                        Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
                        boolean isValid = isValid(minMaxSetting, limitAndCounter[0] , shift.getMinutes());
                        if (!isValid) {
                            if (limitAndCounter[1] != null) {
                                int counterValue = limitAndCounter[1] - 1;
                                if (counterValue < 0) {
                                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                                }else {
                                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,limitAndCounter[1],true,true);
                                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                                }
                            }else {
                                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                                infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                            }
                        }
                    }
                }
            }
        }
    }

    public ShiftLengthWTATemplate(String name, String description, long timeLimit) {
        super(name, description);
        this.timeLimit = timeLimit;
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }
}