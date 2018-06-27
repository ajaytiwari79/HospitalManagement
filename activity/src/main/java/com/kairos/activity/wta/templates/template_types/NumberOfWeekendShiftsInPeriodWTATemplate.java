package com.kairos.activity.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.MinMaxSetting;
import com.kairos.enums.PartOfDay;
import com.kairos.enums.WTATemplateType;
import com.kairos.activity.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.dto.ShiftWithActivityDTO;
import com.kairos.util.DateTimeInterval;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.util.WTARuleTemplateValidatorUtility.*;
import static com.kairos.util.WTARuleTemplateValidatorUtility.isValid;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfWeekendShiftsInPeriodWTATemplate extends WTABaseRuleTemplate {


    private String fromDayOfWeek; //(day of week)
    private LocalTime fromTime;
    private LocalTime toTime;
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

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public String getToDayOfWeek() {
        return toDayOfWeek;
    }

    public void setToDayOfWeek(String toDayOfWeek) {
        this.toDayOfWeek = toDayOfWeek;
    }

    public String getFromDayOfWeek() {
        return fromDayOfWeek;
    }

    public void setFromDayOfWeek(String fromDayOfWeek) {
        this.fromDayOfWeek = fromDayOfWeek;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }


    public NumberOfWeekendShiftsInPeriodWTATemplate(String name, boolean disabled,
                                                    String description, String fromDayOfWeek, LocalTime fromTime,
                                                    String toDayOfWeek, LocalTime toTime) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
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
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)) {
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

    public List<DateTimeInterval> getWeekendsIntervals(DateTimeInterval dateTimeInterval){
        List<DateTimeInterval> intervals = new ArrayList<>();
        ZonedDateTime endDate = null;
        ZonedDateTime startDate = dateTimeInterval.getStart();
        while (true){
            endDate = startDate.plusDays(1);
            ZonedDateTime nextStart = getDayByDate(startDate,fromDayOfWeek,fromTime);
            ZonedDateTime nextEnd = getDayByDate(endDate,toDayOfWeek,toTime);
            if(endDate.isAfter(nextStart) && startDate.isBefore(nextEnd)) {
                intervals.add(new DateTimeInterval(startDate.plusDays(1), endDate.plusDays(1)));
            }
            if(startDate.isAfter(dateTimeInterval.getEnd())) {
                break;
            }
            startDate = endDate;
        }
        return intervals;
    }

    public ZonedDateTime getDayByDate(ZonedDateTime dateTime, String day, LocalTime localTime){
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(day))).with(localTime);
    }

    private int getDayOFF(List<DateTimeInterval> intervals,DateTimeInterval dateTimeInterval){
        List<DateTimeInterval> dayIntervals = getWeekendsIntervals(dateTimeInterval);
        Set<DateTimeInterval> overLapsIntervals = new HashSet<>();
        for (DateTimeInterval interval : intervals) {
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
