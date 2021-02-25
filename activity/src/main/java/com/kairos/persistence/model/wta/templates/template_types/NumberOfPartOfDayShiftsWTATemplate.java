package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
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

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.enums.wta.MinMaxSetting.MINIMUM;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE9
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NumberOfPartOfDayShiftsWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private transient DateTimeInterval interval;

    public NumberOfPartOfDayShiftsWTATemplate(String name, boolean disabled, String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.NUMBER_OF_PARTOFDAY;
    }
    public NumberOfPartOfDayShiftsWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_PARTOFDAY;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidShift(infoWrapper.getPhaseId(),infoWrapper.getShift(),this.phaseTemplateValues,timeTypeIds,plannedTimeIds) && (MINIMUM.equals(minMaxSetting) && ShiftOperationType.DELETE.equals(infoWrapper.getShiftOperationType())) || (MAXIMUM.equals(minMaxSetting) && !ShiftOperationType.DELETE.equals(infoWrapper.getShiftOperationType()))){
            TimeInterval[] timeIntervals = getTimeSlotsByPartOfDay(partOfDays,infoWrapper.getTimeSlotWrapperMap(),infoWrapper.getShift());
            if(timeIntervals.length>0) {
                DateTimeInterval[] dateTimeIntervals = getIntervalsByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper,phaseTemplateValues,this);
                for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                    boolean isValid = validateRule(dateTimeInterval, infoWrapper.getShifts(), timeIntervals,limitAndCounter[0],isNull(infoWrapper.getShift().getId()));
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                            limitAndCounter[2], AppConstants.SHIFT_S,String.valueOf(limitAndCounter[0]));
                    if(!isValid){
                        break;
                    }
                }
            }
        }
    }

    public boolean validateRule(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, TimeInterval[] timeIntervals, int value, boolean includeCurrentShiftCount) {
        int count = 0;
        if(includeCurrentShiftCount){
            count+=1;
        }
        for (ShiftWithActivityDTO shift : shifts) {
            for (TimeInterval timeInterval : timeIntervals) {
                boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
                if (isValidShift && (dateTimeInterval.contains(shift.getStartDate()) || dateTimeInterval.getEndDate().equals(shift.getStartDate())) && (timeInterval == null || timeInterval.contains(DateUtils.asZonedDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY)))) {
                    count++;
                }
                if(!isValid(minMaxSetting, value, count)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = (NumberOfPartOfDayShiftsWTATemplate)wtaBaseRuleTemplate;
        return (this != numberOfPartOfDayShiftsWTATemplate) && !(intervalLength == numberOfPartOfDayShiftsWTATemplate.intervalLength &&
                Float.compare(numberOfPartOfDayShiftsWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, numberOfPartOfDayShiftsWTATemplate.intervalUnit) &&
                Objects.equals(timeTypeIds, numberOfPartOfDayShiftsWTATemplate.timeTypeIds) &&
                Objects.equals(plannedTimeIds, numberOfPartOfDayShiftsWTATemplate.plannedTimeIds) &&
                Objects.equals(partOfDays, numberOfPartOfDayShiftsWTATemplate.partOfDays) &&
                minMaxSetting == numberOfPartOfDayShiftsWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,numberOfPartOfDayShiftsWTATemplate.phaseTemplateValues));
    }

}
