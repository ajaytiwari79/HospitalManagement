package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.utils.ShiftValidatorService.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VetoPerPeriodWTATemplate extends WTABaseRuleTemplate {

    private List<BigInteger> activityIds = new ArrayList<>();
    private int numberOfWeeks;
    private LocalDate validationStartDate;
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private Long vetoPerPeriod;

    public int getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public LocalDate getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(LocalDate validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }


    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {

        this.activityIds = activityIds;
    }


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public Long getVetoPerPeriod() {
        return vetoPerPeriod;
    }

    public void setVetoPerPeriod(Long vetoPerPeriod) {
        this.vetoPerPeriod = vetoPerPeriod;
    }

    public VetoPerPeriodWTATemplate(String name, boolean disabled,
                                    String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        wtaTemplateType = WTATemplateType.VETO_PER_PERIOD;

    }

    public VetoPerPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.VETO_PER_PERIOD;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        String exception = "";
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhase(), this.phaseTemplateValues) && activityIds.contains(infoWrapper.getShift().getActivities().get(0).getActivity().getId())) {
            DateTimeInterval interval = getIntervalByNumberOfWeeks(infoWrapper.getShift(), numberOfWeeks, validationStartDate);
            List<ShiftWithActivityDTO> shifts = getShiftsByInterval(interval, infoWrapper.getShifts(), null);
            shifts = filterShifts(shifts, null, null, activityIds);
            shifts.add(infoWrapper.getShift());
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
            boolean isValid = isValid(minMaxSetting, limitAndCounter[0], shifts.size());
            if (!isValid) {
                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
            }
        }
    }

}
