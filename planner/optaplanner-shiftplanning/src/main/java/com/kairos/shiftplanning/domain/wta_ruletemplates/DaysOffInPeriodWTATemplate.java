package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    @Autowired
    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;
    private boolean restingTimeAllowed;
    private int restingTime;
    private float recommendedValue;

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

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if (!isDisabled() && isValidForPhase(unit.getPhase().getId(),this.phaseTemplateValues)) {
            int count = 0;
            DateTimeInterval[] dateTimeIntervals = getIntervalsByRuleTemplate(shiftImp, intervalUnit, intervalLength);
            for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                shiftImps = getShiftsByInterval(dateTimeInterval, shiftImps);
                shiftImps.add(shiftImp);
                List<DateTimeInterval> intervals = getSortedIntervals(shiftImps);
                if (intervals.size() > 0) {
                    count = getDayOFF(intervals, dateTimeInterval);
                    int limit = getValueByPhaseAndCounter(unit, phaseTemplateValues);
                    penality = isValid(minMaxSetting, limit, count-1);
                }
            }
        }
        return penality;
    }

    private List<DateTimeInterval> getSortedIntervals(List<ShiftImp> shifts) {
        List<DateTimeInterval> intervals = new ArrayList<>();
        for (ShiftImp s : sortShifts(shifts)) {
            if(restingTimeAllowed){
                intervals.add(new DateTimeInterval(s.getStart(),s.getEnd().plusMinutes(s.getRestingMinutes())));
            }else {
                intervals.add(s.getDateTimeInterval());
            }
        }
        return intervals;
    }


    private int getDayOFF(List<DateTimeInterval> intervals,DateTimeInterval dateTimeInterval){
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

    private List<ShiftImp> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftImp> shifts) {
        List<ShiftImp> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if (dateTimeInterval.contains(s.getStartDate()) || dateTimeInterval.contains(s.getEndDate())) {
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }
}
