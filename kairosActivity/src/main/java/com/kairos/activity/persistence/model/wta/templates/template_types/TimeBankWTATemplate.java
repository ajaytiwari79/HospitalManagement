package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.persistence.model.enums.TimeBankTypeEnum;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getValueByPhase;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValidForPhase;


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
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)){
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, getId());
            boolean isValid = isValid(minMaxSetting, limitAndCounter[0], infoWrapper.getTotalTimeBank()/60);
            if (!isValid) {
                if (limitAndCounter[1] != null) {
                    int counterValue = limitAndCounter[1] - 1;
                    if (counterValue < 0) {
                        throw new InvalidRequestException(getName() + " is Broken");
                    } else {
                        infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                        infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                    }
                } else {
                    throw new InvalidRequestException(getName() + " is Broken");
                }
            }
        }
        return "";
    }

    public TimeBankWTATemplate(String name, boolean disabled, String description) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        wtaTemplateType = WTATemplateType.TIME_BANK;
    }

}
