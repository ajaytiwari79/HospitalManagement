package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveWorkWTATemplate extends WTABaseRuleTemplate {

    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private int intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(int intervalLength) {
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


    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
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



    public ConsecutiveWorkWTATemplate() {
        wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
    }


    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)) {
            if (CollectionUtils.containsAny(timeTypeIds,infoWrapper.getShift().getActivitiesTimeTypeIds())) {
                TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, infoWrapper.getTimeSlotWrapperMap(), infoWrapper.getShift());
                if (timeInterval != null) {
                    List<ShiftWithActivityDTO> shiftQueryResultWithActivities = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                    shiftQueryResultWithActivities = getShiftsByInterval(dateTimeInterval, shiftQueryResultWithActivities, timeInterval);
                    shiftQueryResultWithActivities.add(infoWrapper.getShift());
                    List<LocalDate> shiftDates = getSortedAndUniqueDates(shiftQueryResultWithActivities);
                    int consecutiveDays = getConsecutiveDaysInDate(shiftDates);
                    Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), this);
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], consecutiveDays);
                    brokeRuleTemplate(infoWrapper,limitAndCounter[1],isValid, this);
                }
            }
        }
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
