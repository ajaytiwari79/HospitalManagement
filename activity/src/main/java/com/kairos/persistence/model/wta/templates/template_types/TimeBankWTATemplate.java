package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.enums.MinMaxSetting;
import com.kairos.enums.WTATemplateType;
import com.kairos.util.ShiftValidatorService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

import static com.kairos.util.ShiftValidatorService.getValueByPhase;
import static com.kairos.util.ShiftValidatorService.isValid;
import static com.kairos.util.ShiftValidatorService.isValidForPhase;


/**
 * Created by pavan on 20/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TimeBankWTATemplate extends WTABaseRuleTemplate {
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


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

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public TimeBankWTATemplate() {
        wtaTemplateType = WTATemplateType.TIME_BANK;
        //Default Constructor
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        String exception = "";
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)){
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
            boolean isValid = isValid(minMaxSetting, limitAndCounter[0], infoWrapper.getTotalTimeBank()/60);
            if (!isValid) {
                if (limitAndCounter[1] != null) {
                    int counterValue = limitAndCounter[1] - 1;
                    if (counterValue < 0) {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                        ShiftValidatorService.throwException("message.ruleTemplate.broken",this.name);
                    }else {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,limitAndCounter[1],true,true);
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                    }
                }else {
                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                    ShiftValidatorService.throwException("message.ruleTemplate.broken",this.name);
                }
            }
        }
    }

    public TimeBankWTATemplate(String name, boolean disabled, String description) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        wtaTemplateType = WTATemplateType.TIME_BANK;
    }

}
