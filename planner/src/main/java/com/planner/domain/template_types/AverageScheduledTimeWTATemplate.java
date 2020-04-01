package com.planner.domain.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.ShiftLengthAndAverageSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta.WTABaseRuleTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AverageScheduledTimeWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private Set<PartOfDay> partOfDays = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private ShiftLengthAndAverageSetting shiftLengthAndAverageSetting = ShiftLengthAndAverageSetting.DIFFERENCE_BETWEEN_START_END_TIME;

    public AverageScheduledTimeWTATemplate(String name, boolean disabled,
                                           String description, long intervalLength,String intervalUnit) {
        this.intervalLength = intervalLength;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;
    }


    public void validateRules(Unit unit, ShiftImp shiftImp,List<ShiftImp> shiftImps) {
        if(!isDisabled()) {
            if (intervalLength == 0l || StringUtils.isEmpty(intervalUnit)) {
                throwException("message.ruleTemplate.interval.notNull");
            }
            if (isValidForPhase(unit.getPhase().getId(), this.phaseTemplateValues) && CollectionUtils.containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds()) && CollectionUtils.containsAny(plannedTimeIds,infoWrapper.getShift().getActivitiesPlannedTimeIds())) {
                DateTimeInterval interval = getIntervalByRuleTemplate(shiftImp, intervalUnit, intervalLength);
                shiftImps.add(shiftImp);
                List<ShiftImp> shifts = getShiftsByInterval(interval, shiftImps, null);
                shifts = filterShiftsByPlannedTypeAndTimeTypeIds(shiftImps, timeTypeIds, plannedTimeIds);
                List<DateTimeInterval> intervals = getIntervals(interval);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(unit, phaseTemplateValues, this);
                for (DateTimeInterval dateTimeInterval : intervals) {
                    int totalMin = 0;
                    for (ShiftImp shift : shifts) {
                        if (dateTimeInterval.overlaps(shift.getDateTimeInterval())) {
                            totalMin += getValueAccordingShiftLengthAndAverageSetting(shiftLengthAndAverageSetting, shift);
                        }
                    }
                    int penality = isValid(minMaxSetting, limitAndCounter[0], totalMin/(int)intervalLength);

                }
            }
        }
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
        intervals.add(new DateTimeInterval(nextEnd.minusDays(1),getNextDateOfInterval(nextEnd).minusDays(1)));
        return intervals;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate)wtaBaseRuleTemplate;
        return (this != averageScheduledTimeWTATemplate) && !(intervalLength == averageScheduledTimeWTATemplate.intervalLength &&
                Float.compare(averageScheduledTimeWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, averageScheduledTimeWTATemplate.intervalUnit) &&
                Objects.equals(plannedTimeIds, averageScheduledTimeWTATemplate.plannedTimeIds) &&
                Objects.equals(timeTypeIds, averageScheduledTimeWTATemplate.timeTypeIds) &&
                Objects.equals(partOfDays, averageScheduledTimeWTATemplate.partOfDays) &&
                minMaxSetting == averageScheduledTimeWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,averageScheduledTimeWTATemplate.phaseTemplateValues));
    }

}
