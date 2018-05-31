package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.TimeInterval;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeeklyRestPeriodWTATemplate extends WTABaseRuleTemplate {

    private long continuousWeekRest;
    private long intervalLength;
    private String intervalUnit;
    protected List<PartOfDay> partOfDays = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


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


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public WeeklyRestPeriodWTATemplate(String name, boolean disabled,
                                       String description, long continuousWeekRest) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

        this.continuousWeekRest=continuousWeekRest;
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;

    }

    public WeeklyRestPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.WEEKLY_REST_PERIOD;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrappers(), infoWrapper.getShift());
        if (timeInterval != null) {
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            int totalRestingTime = getTotalRestingTime(infoWrapper.getShifts(), dateTimeInterval);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), getId());
            if (!isValid(minMaxSetting, limitAndCounter[0], totalRestingTime)) {
                if (limitAndCounter[1] != null) {
                    int counterValue = limitAndCounter[1] - 1;
                    if (counterValue < 0) {
                        new InvalidRequestException(getName() + " is Broken");
                        infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                        infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                    }
                } else {
                    new InvalidRequestException(getName() + " is Broken");
                }
            }

        }
        return "";
    }


    public int getTotalRestingTime(List<ShiftWithActivityDTO> shifts, DateTimeInterval dateTimeInterval) {
                if (shifts.size() < 2) return 0;
                int totalRestTime = dateTimeInterval.getMinutes();
                for (ShiftWithActivityDTO shift : shifts) {
                        totalRestTime = shift.getMinutes();
                    }
                return totalRestTime;
            }

}
