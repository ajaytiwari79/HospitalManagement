package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.MinMaxSetting;
import com.kairos.persistence.enums.PartOfDay;
import com.kairos.persistence.enums.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.dto.ShiftWithActivityDTO;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.TimeInterval;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.util.WTARuleTemplateValidatorUtility.*;
import static com.kairos.util.WTARuleTemplateValidatorUtility.getValueByPhase;
import static com.kairos.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftsInIntervalWTATemplate extends WTABaseRuleTemplate {
    private long intervalLength;//
    private String intervalUnit;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    protected List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY,PartOfDay.EVENING,PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }


    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
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


    public ShiftsInIntervalWTATemplate(String name,  boolean disabled,
                                       String description, long intervalLength, String intervalUnit) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.NUMBER_OF_SHIFTS_IN_INTERVAL;

    }
    public ShiftsInIntervalWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_SHIFTS_IN_INTERVAL;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && (plannedTimeIds.contains(infoWrapper.getShift().getPlannedTypeId()) && timeTypeIds.contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))){
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays,infoWrapper.getTimeSlotWrappers(),infoWrapper.getShift());
            if(timeInterval!=null) {
                DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                List<ShiftWithActivityDTO> shifts = filterShifts(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds, null);
                shifts = getShiftsByInterval(dateTimeInterval, shifts, timeInterval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper,phaseTemplateValues,getId());
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], shifts.size());
                if (!isValid) {
                    if(limitAndCounter[1]!=null) {
                        int counterValue =  limitAndCounter[1] - 1;
                        if(counterValue<0){
                            throw new InvalidRequestException(getName() + " is Broken");
                        }else {
                            infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                            infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                        }
                    }else {
                        throw new InvalidRequestException(getName() + " is Broken");
                    }
                }
            }
        }
        return "";
    }
}
