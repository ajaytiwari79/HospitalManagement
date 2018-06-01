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

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfWeekendShiftsInPeriodWTATemplate extends WTABaseRuleTemplate {

    private long numberShiftsPerPeriod;
    private long numberOfWeeks;
    private String fromDayOfWeek; //(day of week)
    private long fromTime;
    private boolean proportional;
    private long toTime;
    private String toDayOfWeek;
    private long intervalLength;
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;

    protected List<PartOfDay> partOfDays = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

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

    public boolean isProportional() {
        return proportional;
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

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public String getToDayOfWeek() {
        return toDayOfWeek;
    }

    public void setToDayOfWeek(String toDayOfWeek) {
        this.toDayOfWeek = toDayOfWeek;
    }

    public long getNumberShiftsPerPeriod() {
        return numberShiftsPerPeriod;
    }

    public void setNumberShiftsPerPeriod(long numberShiftsPerPeriod) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
    }

    public long getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(long numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public String getFromDayOfWeek() {
        return fromDayOfWeek;
    }

    public void setFromDayOfWeek(String fromDayOfWeek) {
        this.fromDayOfWeek = fromDayOfWeek;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public boolean getProportional() {
        return proportional;
    }

    public void setProportional(boolean proportional) {
        this.proportional = proportional;
    }

    public NumberOfWeekendShiftsInPeriodWTATemplate(String name, boolean disabled,
                                                    String description, long numberShiftsPerPeriod, long numberOfWeeks, String fromDayOfWeek, long fromTime, boolean proportional,
                                                    String toDayOfWeek, long toTime) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
        this.numberOfWeeks = numberOfWeeks;
        this.proportional = proportional;
        this.fromDayOfWeek = fromDayOfWeek;
        this.fromTime = fromTime;
        this.toDayOfWeek = toDayOfWeek;
        this.toTime = toTime;
        wtaTemplateType = WTATemplateType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;


    }

    public NumberOfWeekendShiftsInPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;

    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrappers(), infoWrapper.getShift());
        if (timeInterval != null) {
            int count = 0;
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            if (intervals.size() > 2) {
                for (int i = 1; i < intervals.size(); i++) {
                    DateTimeInterval interval = intervals.get(i - 1);
                    interval = isRestingTimeAllowed ? getNextDayInterval(interval.getEnd().plusHours(restingTime)) : getNextDayInterval(interval.getEnd());

                    if (!interval.overlaps(intervals.get(i))) {
                        count++;
                    }
                }
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, getId());
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count);
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

    private DateTimeInterval getNextDayInterval(ZonedDateTime dateTime) {
        return new DateTimeInterval(dateTime.plusDays(1).truncatedTo(ChronoUnit.DAYS), dateTime.plusDays(2).truncatedTo(ChronoUnit.DAYS));
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

    private DateTimeInterval getIntervalOFWeekEnd(DateTimeInterval interval) {
        return new DateTimeInterval(interval.getStart().with(DayOfWeek.valueOf(fromDayOfWeek)).plusHours(fromTime), interval.getEnd().with(DayOfWeek.valueOf(toDayOfWeek)).plusHours(toTime));

    }


}
