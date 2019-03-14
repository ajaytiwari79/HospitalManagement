package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kairos.constants.AppConstants.HOURS;
import static com.kairos.service.shift.ShiftValidatorService.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftsInIntervalWTATemplate extends WTABaseRuleTemplate {
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;//
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    protected List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY,PartOfDay.EVENING,PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }


    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
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


    public ShiftsInIntervalWTATemplate(String name,  boolean disabled,
                                       String description, long intervalLength, String intervalUnit) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.NUMBER_OF_SHIFTS_IN_INTERVAL;

    }
    public ShiftsInIntervalWTATemplate() {
        wtaTemplateType = WTATemplateType.NUMBER_OF_SHIFTS_IN_INTERVAL;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(),this.phaseTemplateValues) && timeTypeIds.contains(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeTypeId())){
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays,infoWrapper.getTimeSlotWrapperMap(),infoWrapper.getShift());
            if(timeInterval!=null) {
                DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                List<ShiftWithActivityDTO> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
                shifts = getShiftsByInterval(dateTimeInterval, shifts, timeInterval);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper,phaseTemplateValues,this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], shifts.size());
                brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this.id,this.name+" - "+limitAndCounter[0]/60+" "+ HOURS,limitAndCounter[2]);
            }
        }
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate) wtaBaseRuleTemplate;
        return (this != shiftsInIntervalWTATemplate) && !(intervalLength == shiftsInIntervalWTATemplate.intervalLength &&
                Float.compare(shiftsInIntervalWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, shiftsInIntervalWTATemplate.intervalUnit) &&
                Objects.equals(timeTypeIds, shiftsInIntervalWTATemplate.timeTypeIds) &&
                Objects.equals(plannedTimeIds, shiftsInIntervalWTATemplate.plannedTimeIds) &&
                Objects.equals(partOfDays, shiftsInIntervalWTATemplate.partOfDays) &&
                minMaxSetting == shiftsInIntervalWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,shiftsInIntervalWTATemplate.phaseTemplateValues));
    }

}
