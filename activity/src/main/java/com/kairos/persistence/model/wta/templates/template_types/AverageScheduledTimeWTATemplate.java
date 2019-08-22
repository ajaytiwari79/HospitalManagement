package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.service.shift.ShiftValidatorService.filterShiftsByPlannedTypeAndTimeTypeIds;
import static com.kairos.service.shift.ShiftValidatorService.throwException;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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





    public AverageScheduledTimeWTATemplate(String name, boolean disabled,
                                           String description, long intervalLength,String intervalUnit) {
        this.intervalLength = intervalLength;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;

    }



    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public AverageScheduledTimeWTATemplate() {
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled()) {
            if (intervalLength == 0l || StringUtils.isEmpty(intervalUnit)) {
                throwException("message.ruleTemplate.interval.notNull");
            }
            if (isValidForPhase(infoWrapper.getPhaseId(), this.phaseTemplateValues) && CollectionUtils.containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds()) && CollectionUtils.containsAny(plannedTimeIds,infoWrapper.getShift().getActivitiesPlannedTimeIds())) {
                DateTimeInterval interval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                infoWrapper.getShifts().add(infoWrapper.getShift());
                List<ShiftWithActivityDTO> shifts = getShiftsByInterval(interval, infoWrapper.getShifts(), null);
                shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
                List<DateTimeInterval> intervals = getIntervals(interval);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, phaseTemplateValues, this);
                for (DateTimeInterval dateTimeInterval : intervals) {
                    int totalMin = 0;
                    for (ShiftWithActivityDTO shift : shifts) {
                        if (dateTimeInterval.overlaps(shift.getDateTimeInterval())) {
                            totalMin += (int) dateTimeInterval.overlap(shift.getDateTimeInterval()).getMinutes();
                        }
                    }
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], totalMin/(int)intervalLength);
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,limitAndCounter[2], DurationType.HOURS,getHoursByMinutes(limitAndCounter[0],this.name));
                    if(!isValid){
                        break;
                    }
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
