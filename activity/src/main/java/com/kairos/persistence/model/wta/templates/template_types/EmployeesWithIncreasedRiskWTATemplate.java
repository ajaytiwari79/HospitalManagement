package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.util.Objects;

public class EmployeesWithIncreasedRiskWTATemplate extends WTABaseRuleTemplate {

    private int belowAge;
    private int aboveAge;
    private boolean pregnant;
    private boolean restingTimeAllowed;
    private int restingTime;

    public EmployeesWithIncreasedRiskWTATemplate() {
        wtaTemplateType = WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        //This is override method
    }

    public EmployeesWithIncreasedRiskWTATemplate(String name, boolean disabled, String description, int belowYear, int aboveYear, boolean pregnant) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.belowAge = belowYear;
        this.aboveAge = aboveYear;
        this.pregnant = pregnant;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        EmployeesWithIncreasedRiskWTATemplate employeesWithIncreasedRiskWTATemplate = (EmployeesWithIncreasedRiskWTATemplate)wtaBaseRuleTemplate;
        return (this != employeesWithIncreasedRiskWTATemplate) && !(belowAge == employeesWithIncreasedRiskWTATemplate.belowAge &&
                aboveAge == employeesWithIncreasedRiskWTATemplate.aboveAge &&
                pregnant == employeesWithIncreasedRiskWTATemplate.pregnant &&
                restingTimeAllowed == employeesWithIncreasedRiskWTATemplate.restingTimeAllowed &&
                restingTime == employeesWithIncreasedRiskWTATemplate.restingTime && Objects.equals(this.phaseTemplateValues,employeesWithIncreasedRiskWTATemplate.phaseTemplateValues));
    }
}
