package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.CommonConstants.*;
import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ShortestAndAverageDailyRestWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private float recommendedValue;
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();


    public ShortestAndAverageDailyRestWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SHORTEST_AND_AVERAGE_DAILY_REST;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && isValidForPhase(unit.getPlanningPeriod().getPhase().getId(),this.phaseTemplateValues)  && CollectionUtils.containsAny(timeTypeIds,shiftImp.getActivitiesTimeTypeIds())){
            DateTimeInterval interval = getIntervalByRuleTemplate(shiftImp,intervalUnit,intervalLength);
            shiftImps = filterShiftsByPlannedTypeAndTimeTypeIds(shiftImps,timeTypeIds,plannedTimeIds);
            shiftImps = getShiftsByInterval(interval,shiftImps,null);
            shiftImps.add(shiftImp);
            List<DateTimeInterval> intervals = getIntervals(interval);
            for (DateTimeInterval dateTimeInterval : intervals) {
                int totalMin = (int)dateTimeInterval.getMinutes();
                for (ShiftImp shift : shiftImps) {
                    if(dateTimeInterval.overlaps(shift.getDateTimeInterval())){
                        totalMin -= (int)dateTimeInterval.overlap(shift.getDateTimeInterval()).getMinutes();
                    }
                }
                int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                penality = isValid(MAXIMUM, limit, totalMin);
            }
        }
        return penality;
    }

    public ZonedDateTime getNextDateOfInterval(ZonedDateTime dateTime){
        ZonedDateTime zonedDateTime = null;
        switch (intervalUnit){
            case DAYS:zonedDateTime = dateTime.plusDays(intervalLength);
                break;
            case WEEKS:zonedDateTime = dateTime.plusWeeks(intervalLength);
                break;
            case MONTHS:zonedDateTime = dateTime.plusMonths(intervalLength);
                break;
            case YEARS:zonedDateTime = dateTime.plusYears(intervalLength);
                break;
            default:
                break;
        }
        return zonedDateTime;
    }

    private List<DateTimeInterval> getIntervals(DateTimeInterval interval){
        List<DateTimeInterval> intervals = new ArrayList<>();
        ZonedDateTime nextEnd = getNextDateOfInterval(interval.getStart());
        intervals.add(new DateTimeInterval(interval.getStart(),nextEnd));
        intervals.add(new DateTimeInterval(nextEnd,getNextDateOfInterval(nextEnd)));
        return intervals;
    }



}
