package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asZoneDateTime;
import static com.kairos.constants.AppConstants.DAYS;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(),this.phaseTemplateValues)) {
            int count = 0;
            DateTimeInterval[] dateTimeIntervals = getIntervalsByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
            for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
               // DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
               // dateTimeInterval = new DateTimeInterval(dateTimeInterval.getStart().minusDays(1), dateTimeInterval.getEnd().plusDays(1));
                List<ShiftWithActivityDTO> shifts = getShiftsByInterval(dateTimeInterval, infoWrapper.getShifts());
                shifts.add(infoWrapper.getShift());
                List<DateTimeInterval> intervals = getSortedIntervals(shifts);
                if (intervals.size() > 0) {
                    count = getDayOFF(intervals, dateTimeInterval);
                    Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, phaseTemplateValues, this);
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], count-1);
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper, limitAndCounter[1], isValid, this,
                            limitAndCounter[2], DurationType.DAYS, String.valueOf(limitAndCounter[0]));
                }
            }
        }
    }

    private List<DateTimeInterval> getSortedIntervals(List<ShiftWithActivityDTO> shifts) {
        List<DateTimeInterval> intervals = new ArrayList<>();
        for (ShiftWithActivityDTO s : sortShifts(shifts)) {
            if(restingTimeAllowed){
                intervals.add(new DateTimeInterval(s.getStartDate(),asDate(asZoneDateTime(s.getEndDate()).plusMinutes(s.getRestingMinutes()))));
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
        DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate)wtaBaseRuleTemplate;
        return (this != daysOffInPeriodWTATemplate) && !(intervalLength == daysOffInPeriodWTATemplate.intervalLength &&
                restingTimeAllowed == daysOffInPeriodWTATemplate.restingTimeAllowed &&
                restingTime == daysOffInPeriodWTATemplate.restingTime &&
                Float.compare(daysOffInPeriodWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, daysOffInPeriodWTATemplate.intervalUnit) &&
                minMaxSetting == daysOffInPeriodWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,daysOffInPeriodWTATemplate.phaseTemplateValues));
    }
}
