package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;


/**
 * Created by pavan on 20/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TimeBankWTATemplate extends WTABaseRuleTemplate {
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
    public TimeBankWTATemplate() {
        wtaTemplateType = WTATemplateType.TIME_BANK;
        //Default Constructor
    }

    public void validateRules(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        if(!isDisabled() && this.minMaxSetting.equals(MinMaxSetting.MAXIMUM) && isValidForPhase(unit.getPhase().getId(),this.phaseTemplateValues)){
            int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
            int penality = isValid(MAXIMUM, limit, 0);
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
