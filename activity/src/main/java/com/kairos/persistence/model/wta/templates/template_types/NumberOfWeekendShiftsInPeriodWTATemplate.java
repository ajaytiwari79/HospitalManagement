package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.getAllDaysBetweenDays;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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
    private transient DateTimeInterval interval;


    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

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
        this.wtaTemplateType = WTATemplateType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;

    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(), this.phaseTemplateValues)) {
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            List<DateTimeInterval> intervals = getSortedIntervals(dateTimeInterval, infoWrapper.getShifts());
            int count = getDayOFF(intervals, dateTimeInterval);
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, phaseTemplateValues, this);
            boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count);
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper, limitAndCounter[1], isValid, this,
                    limitAndCounter[2], DurationType.DAYS.toValue(), String.valueOf(limitAndCounter[0]));
        }
    }

    public List<DateTimeInterval> getWeekendsIntervals(DateTimeInterval dateTimeInterval) {
        List<DateTimeInterval> intervals = new ArrayList<>();
        ZonedDateTime startDate = dateTimeInterval.getStart();
        ZonedDateTime endDate = dateTimeInterval.getEnd();
        DayOfWeek startDayOfWeek = LocalTime.MIN.equals(fromTime) ? DayOfWeek.valueOf(fromDayOfWeek) : DayOfWeek.valueOf(fromDayOfWeek).plus(1);
        DayOfWeek endDayOfWeek = LocalTime.MAX.equals(toTime) ? DayOfWeek.valueOf(toDayOfWeek) : DayOfWeek.valueOf(toDayOfWeek).minus(1);
        Set<DayOfWeek> dayOfWeeks = getAllDaysBetweenDays(startDayOfWeek, endDayOfWeek);
        while (startDate.isBefore(endDate)) {
            if (dayOfWeeks.contains(startDate.getDayOfWeek())) {
                intervals.add(new DateTimeInterval(startDate, startDate.plusDays(1)));
            }
            startDate = startDate.plusDays(1);
        }
        return intervals;
    }

    private int getDayOFF(List<DateTimeInterval> intervals, DateTimeInterval dateTimeInterval) {
        List<DateTimeInterval> dayIntervals = getWeekendsIntervals(dateTimeInterval);
        Set<DateTimeInterval> overLapsIntervals = new HashSet<>();
        for (DateTimeInterval interval : intervals) {
            if (restingTimeAllowed) {
                interval = new DateTimeInterval(interval.getStart(), interval.getEnd().plusHours(restingTime));
            }
            overLapsIntervals.addAll(getOverLapsInterval(dayIntervals, interval));
        }
        return dayIntervals.size() - overLapsIntervals.size();
    }

    private List<DateTimeInterval> getOverLapsInterval(List<DateTimeInterval> intervals, DateTimeInterval dateTimeInterval) {
        List<DateTimeInterval> overLapIntervals = new ArrayList<>();
        intervals.forEach(interval -> {
            if (interval.overlaps(dateTimeInterval)) {
                overLapIntervals.add(interval);
            }
        });
        return overLapIntervals;
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
                minMaxSetting == numberOfWeekendShiftsInPeriodWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues, numberOfWeekendShiftsInPeriodWTATemplate.phaseTemplateValues));
    }

}
