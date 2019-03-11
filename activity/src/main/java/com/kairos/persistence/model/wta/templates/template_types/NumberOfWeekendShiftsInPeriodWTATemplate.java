package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.kairos.constants.CommonConstants.DAYS;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


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
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;


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
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)) {
            int count = 0;
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            if (intervals.size() > 2) {
                count = getDayOFF(intervals,dateTimeInterval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count);
                brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this.id,this.name+" - "+limitAndCounter[0]+" "+ DAYS);
            }
        }
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

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate) wtaBaseRuleTemplate;
        return (this != numberOfWeekendShiftsInPeriodWTATemplate) && !(intervalLength == numberOfWeekendShiftsInPeriodWTATemplate.intervalLength &&
                restingTimeAllowed == numberOfWeekendShiftsInPeriodWTATemplate.restingTimeAllowed &&
                restingTime == numberOfWeekendShiftsInPeriodWTATemplate.restingTime &&
                Float.compare(numberOfWeekendShiftsInPeriodWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(fromDayOfWeek, numberOfWeekendShiftsInPeriodWTATemplate.fromDayOfWeek) &&
                Objects.equals(fromTime, numberOfWeekendShiftsInPeriodWTATemplate.fromTime) &&
                Objects.equals(toTime, numberOfWeekendShiftsInPeriodWTATemplate.toTime) &&
                Objects.equals(toDayOfWeek, numberOfWeekendShiftsInPeriodWTATemplate.toDayOfWeek) &&
                Objects.equals(intervalUnit, numberOfWeekendShiftsInPeriodWTATemplate.intervalUnit) &&
                minMaxSetting == numberOfWeekendShiftsInPeriodWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,numberOfWeekendShiftsInPeriodWTATemplate.phaseTemplateValues));
    }

}
