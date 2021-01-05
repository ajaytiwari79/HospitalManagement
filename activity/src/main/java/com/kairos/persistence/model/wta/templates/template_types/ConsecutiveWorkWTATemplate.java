package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.shift.ShiftOperationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.temporal.ChronoField;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.enums.wta.MinMaxSetting.MINIMUM;
import static com.kairos.service.shift.ShiftValidatorService.filterShiftsByPlannedTypeAndTimeTypeIds;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ConsecutiveWorkWTATemplate extends WTABaseRuleTemplate {

    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private int intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private transient DateTimeInterval interval;

    public ConsecutiveWorkWTATemplate() {
        this.wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
    }


    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(),this.phaseTemplateValues) && (MINIMUM.equals(minMaxSetting) && ShiftOperationType.DELETE.equals(infoWrapper.getShiftOperationType())) || (MAXIMUM.equals(minMaxSetting) && !ShiftOperationType.DELETE.equals(infoWrapper.getShiftOperationType()))) {
            if (CollectionUtils.containsAny(timeTypeIds,infoWrapper.getShift().getActivitiesTimeTypeIds())) {
                List<TimeInterval> timeIntervals = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrapperMap(), null);
                if (isCollectionNotEmpty(timeIntervals)) {
                    List<ShiftWithActivityDTO> shiftQueryResultWithActivities = getShiftsByInterval(infoWrapper.getShifts(), timeIntervals);
                    if(MAXIMUM.equals(minMaxSetting)){
                        shiftQueryResultWithActivities.add(infoWrapper.getShift());
                    }
                    int consecutiveDays = getConsecutiveDaysInDate(new ArrayList<>(getSortedAndUniqueDates(shiftQueryResultWithActivities)));
                    Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], consecutiveDays);
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                            limitAndCounter[2], DurationType.DAYS.toValue(),String.valueOf(limitAndCounter[0]));
                }
            }
        }
    }

    public List<ShiftWithActivityDTO> getShiftsByInterval(List<ShiftWithActivityDTO> shifts, List<TimeInterval> timeIntervals) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
            if ((interval.contains(shift.getStartDate()) || interval.getEndLocalDate().equals(shift.getEndLocalDate())) && isTimeIntervalValid(timeIntervals, shift) && isValidShift) {
                updatedShifts.add(shift);
            }
        });
        return updatedShifts;
    }

    private boolean isTimeIntervalValid(List<TimeInterval> timeIntervals, ShiftWithActivityDTO s) {
        return timeIntervals == null || timeIntervals.stream().anyMatch(timeInterval->timeInterval.contains(DateUtils.asZonedDateTime(s.getStartDate()).get(ChronoField.MINUTE_OF_DAY)));
    }

    public ConsecutiveWorkWTATemplate(String name, String description) {
        super(name, description);
        this.wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
    }


    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = (ConsecutiveWorkWTATemplate)wtaBaseRuleTemplate;
        return (this != consecutiveWorkWTATemplate) && !(Float.compare(consecutiveWorkWTATemplate.recommendedValue, recommendedValue) == 0 &&
                intervalLength == consecutiveWorkWTATemplate.intervalLength &&
                Objects.equals(partOfDays, consecutiveWorkWTATemplate.partOfDays) &&
                Objects.equals(plannedTimeIds, consecutiveWorkWTATemplate.plannedTimeIds) &&
                Objects.equals(timeTypeIds, consecutiveWorkWTATemplate.timeTypeIds) &&
                minMaxSetting == consecutiveWorkWTATemplate.minMaxSetting &&
                Objects.equals(intervalUnit, consecutiveWorkWTATemplate.intervalUnit) && Objects.equals(this.phaseTemplateValues,consecutiveWorkWTATemplate.phaseTemplateValues));
    }

}
