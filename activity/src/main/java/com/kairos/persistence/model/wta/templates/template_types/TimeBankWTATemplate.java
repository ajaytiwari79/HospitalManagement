package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.TimeBankLimitsType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


/**
 * Created by pavan on 20/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Getter
@Setter
public class TimeBankWTATemplate extends WTABaseRuleTemplate {
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private boolean staffCanIgnoreForWeeklyEmployment;
    private boolean managementCanIgnoreForWeeklyEmployment;
    private int factorOfWeeklyEmploymentForStaff;
    private int factorOfWeeklyEmploymentForManagement;
    private TimeBankLimitsType timeBankLimitsType;

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public TimeBankWTATemplate() {
        wtaTemplateType = WTATemplateType.TIME_BANK;
        //Default Constructor
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && this.minMaxSetting.equals(MinMaxSetting.MAXIMUM) && isValidForPhase(infoWrapper.getPhaseId(),this.phaseTemplateValues)){
            Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, phaseTemplateValues, this);
            if(isNull(timeBankLimitsType) && isNotNull(infoWrapper.getUnitGeneralSetting())){
                timeBankLimitsType = infoWrapper.getUnitGeneralSetting().getTimeBankLimitsType();
            }
            int limit;
            if(TimeBankLimitsType.FACTOR_OF_WEEKLY_HOURS.equals(timeBankLimitsType)){
                limit = infoWrapper.getTotalWeeklyMinutes() * (UserContext.getUserDetails().isStaff() ? factorOfWeeklyEmploymentForStaff : factorOfWeeklyEmploymentForManagement);
            } else {
                limit = limitAndCounter[0]*60;
            }
            boolean isValid = isValid(minMaxSetting, limit, (int)infoWrapper.getTotalTimeBank());
            brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                    limitAndCounter[2], DurationType.HOURS.toValue(),getHoursByMinutes(limit,this.name));
        }
    }

    public TimeBankWTATemplate(String name, boolean disabled, String description) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        wtaTemplateType = WTATemplateType.TIME_BANK;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        TimeBankWTATemplate timeBankWTATemplate = (TimeBankWTATemplate) wtaBaseRuleTemplate;
        return (this != timeBankWTATemplate) && !(Float.compare(timeBankWTATemplate.recommendedValue, recommendedValue) == 0 &&
                minMaxSetting == timeBankWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,timeBankWTATemplate.phaseTemplateValues));
    }

}
