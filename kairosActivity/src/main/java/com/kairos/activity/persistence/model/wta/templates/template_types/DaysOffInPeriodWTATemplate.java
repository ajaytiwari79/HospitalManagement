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
import org.joda.time.DateTime;


import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private boolean restingTimeAllowed;
    private int restingTime;
    private float recommendedValue;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public boolean isRestingTimeAllowed() {
        return restingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        this.restingTimeAllowed = restingTimeAllowed;
    }
    public int getRestingTime() {
        return restingTime;
    }

    public void setRestingTime(int restingTime) {
        this.restingTime = restingTime;
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
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled()) {
            int count = 0;
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            //dateTimeInterval = new DateTimeInterval(dateTimeInterval.getStart().minusDays(1),dateTimeInterval.getEnd().plusDays(1));
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            if (intervals.size() > 2) {
                count = getDayOFF(intervals,dateTimeInterval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, getId());
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count);
                if (!isValid) {
                    if (limitAndCounter[1] != null) {
                        int counterValue = limitAndCounter[1] - 1;
                        if (counterValue < 0) {
                            throw new InvalidRequestException(getName() + " is Broken");
                        } else {
                            infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                            infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                        }
                    } else {
                        throw new InvalidRequestException(getName() + " is Broken");
                    }
                }
            }
        }
        return "";
    }


    private int getDayOFF(List<DateTimeInterval> intervals,DateTimeInterval dateTimeInterval){
        int count = 0;
        List<DateTimeInterval> dayIntervals = getDaysIntervals(dateTimeInterval);
        Set<DateTimeInterval> overLapsIntervals = new HashSet<>();
        for (int i = 1; i < intervals.size(); i++) {
            DateTimeInterval interval = intervals.get(i - 1);
            if(restingTimeAllowed){
                interval = new DateTimeInterval(interval.getStart(),interval.getEnd().plusHours(restingTime));
            }
            overLapsIntervals.addAll(getOverLapsInterval(dayIntervals,interval));

        }
        return dayIntervals.size() - overLapsIntervals.size();
    }

    private List<DateTimeInterval> getOverLapsInterval(List<DateTimeInterval> intervals,DateTimeInterval dateTimeInterval){
        List<DateTimeInterval> overLapIntervals = new ArrayList<>();
        intervals.forEach(interval->{
            if(interval.overlaps(dateTimeInterval)){
                overLapIntervals.add(interval);
            }
        });
        return overLapIntervals;
    }

    private List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if (dateTimeInterval.contains(s.getStartDate()) || dateTimeInterval.contains(s.getEndDate())) {
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }
}
