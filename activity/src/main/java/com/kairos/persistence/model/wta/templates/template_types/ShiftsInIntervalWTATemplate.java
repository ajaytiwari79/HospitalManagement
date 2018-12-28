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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftsInIntervalWTATemplate extends WTABaseRuleTemplate {
    private long intervalLength;//
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
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && timeTypeIds.contains(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeTypeId())){
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays,infoWrapper.getTimeSlotWrapperMap(),infoWrapper.getShift());
            if(timeInterval!=null) {
                DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                List<ShiftWithActivityDTO> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
                shifts = getShiftsByInterval(dateTimeInterval, shifts, timeInterval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper,phaseTemplateValues,this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], shifts.size());
                brokeRuleTemplate(infoWrapper,limitAndCounter[1],isValid, this);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        if (!super.equals(o)) return false;
        ShiftsInIntervalWTATemplate that = (ShiftsInIntervalWTATemplate) o;
        return intervalLength == that.intervalLength &&
                Float.compare(that.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, that.intervalUnit) &&
                Objects.equals(timeTypeIds, that.timeTypeIds) &&
                Objects.equals(plannedTimeIds, that.plannedTimeIds) &&
                Objects.equals(partOfDays, that.partOfDays) &&
                minMaxSetting == that.minMaxSetting;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), intervalLength, intervalUnit, timeTypeIds, plannedTimeIds, partOfDays, recommendedValue, minMaxSetting);
    }
}
