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
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.TimeInterval;


import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private List<PartOfDay> partOfDays = new ArrayList<>();
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private boolean isRestingTimeAllowed;
    private int restingTime;
    private float recommendedValue;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public boolean isRestingTimeAllowed() {
        return isRestingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        this.isRestingTimeAllowed = restingTimeAllowed;
    }

    public int getRestingTime() {
        return restingTime;
    }

    public void setRestingTime(int restingTime) {
        this.restingTime = restingTime;
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

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }



    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }


    public DaysOffInPeriodWTATemplate(String name, boolean disabled,
                                      String description, long intervalLength, String intervalUnit) {
        this.intervalLength = intervalLength;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit = intervalUnit;
        wtaTemplateType = WTATemplateType.DAYS_OFF_IN_PERIOD;

    }

    public DaysOffInPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.DAYS_OFF_IN_PERIOD;
    }

    @Override
    public boolean isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays,infoWrapper.getTimeSlotWrappers(),infoWrapper.getShift());
        if(timeInterval!=null) {
            int count = 0;
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval,infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            if(intervals.size()>2){
                for (int i=1;i<intervals.size();i++){
                    DateTimeInterval interval = intervals.get(i-1);
                        interval = isRestingTimeAllowed ? getNextDayInterval(interval.getEnd().plusHours(restingTime)) : getNextDayInterval(interval.getEnd());

                    if(!interval.overlaps(intervals.get(i))){
                        count++;
                    }
                }
                Integer[] limitAndCounter = getValueByPhase(infoWrapper,phaseTemplateValues,getId());
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count);
                if (!isValid) {
                    if(limitAndCounter[1]!=null) {
                        int counterValue =  limitAndCounter[1] - 1;
                        if(counterValue<0){
                            new InvalidRequestException(getName() + " is Broken");
                            infoWrapper.getCounterMap().put(getId()+"-"+infoWrapper.getPhase(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                            infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                        }
                    }else {
                        new InvalidRequestException(getName() + " is Broken");
                    }
                }
            }
        }
        return false;
    }

    private DateTimeInterval getNextDayInterval(ZonedDateTime dateTime){
        return new DateTimeInterval(dateTime.plusDays(1).truncatedTo(ChronoUnit.DAYS),dateTime.plusDays(2).truncatedTo(ChronoUnit.DAYS));
    }

    private List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval,List<ShiftWithActivityDTO> shifts){
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s->{
            if(dateTimeInterval.contains(s.getStartDate()) || dateTimeInterval.contains(s.getEndDate())){
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }
}
