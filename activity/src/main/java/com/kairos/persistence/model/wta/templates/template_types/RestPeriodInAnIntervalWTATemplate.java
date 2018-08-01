package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.MinMaxSetting;
import com.kairos.enums.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.util.DateTimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.util.WTARuleTemplateValidatorUtility.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPeriodInAnIntervalWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();


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

    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public RestPeriodInAnIntervalWTATemplate(String name, boolean disabled,
                                             String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;

    }

    public RestPeriodInAnIntervalWTATemplate() {
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;
    }

    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        String exception="";
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts(), null);
            shifts.add(infoWrapper.getShift());
            shifts = sortShifts(shifts);
            int maxRestingTime = getMaxRestingTime(shifts);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), this);
            if (!isValid(minMaxSetting, limitAndCounter[0], maxRestingTime/60)) {
                if (limitAndCounter[1] != null) {
                    int counterValue = limitAndCounter[1] - 1;
                    if (counterValue < 0) {
                        exception = getName();                        infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                        infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                    }
                } else {
                    exception = getName();                }
            }
        }
        return exception;
    }


    public int getMaxRestingTime(List<ShiftWithActivityDTO> shifts) {
        int maxRestTime = 0;
        for (int i=1;i<shifts.size();i++) {
            int restTime = new DateTimeInterval(shifts.get(i-1).getEndDate().getTime(),shifts.get(i).getStartDate().getTime()).getMinutes();
            if(restTime>maxRestTime){
                maxRestTime = restTime;
            }
        }
        return maxRestTime;
    }

}
