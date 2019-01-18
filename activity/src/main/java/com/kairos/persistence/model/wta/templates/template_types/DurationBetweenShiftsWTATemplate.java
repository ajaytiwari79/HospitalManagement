package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


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



    public DurationBetweenShiftsWTATemplate(String name, boolean disabled,
                                            String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

    }
    public DurationBetweenShiftsWTATemplate() {
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && timeTypeIds.contains(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeTypeId())) {
            int timefromPrevShift = 0;
            List<ShiftWithActivityDTO> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
            shifts = (List<ShiftWithActivityDTO>) shifts.stream().filter(shift1 -> DateUtils.asZoneDateTime(shift1.getEndDate()).isBefore(DateUtils.asZoneDateTime(infoWrapper.getShift().getStartDate()))).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
            if (!shifts.isEmpty()) {
                ZonedDateTime prevShiftEnd = DateUtils.asZoneDateTime(shifts.get(shifts.size() - 1).getEndDate());
                timefromPrevShift = (int)new DateTimeInterval(prevShiftEnd, DateUtils.asZoneDateTime(infoWrapper.getShift().getStartDate())).getMinutes();
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, getPhaseTemplateValues(), this);
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], timefromPrevShift);
                brokeRuleTemplate(infoWrapper,limitAndCounter[1],isValid, this);
            }
        }
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = (DurationBetweenShiftsWTATemplate)wtaBaseRuleTemplate;
        return (this != durationBetweenShiftsWTATemplate) && !(Float.compare(durationBetweenShiftsWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(plannedTimeIds, durationBetweenShiftsWTATemplate.plannedTimeIds) &&
                Objects.equals(timeTypeIds, durationBetweenShiftsWTATemplate.timeTypeIds) &&
                minMaxSetting == durationBetweenShiftsWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,durationBetweenShiftsWTATemplate.phaseTemplateValues));
    }

}