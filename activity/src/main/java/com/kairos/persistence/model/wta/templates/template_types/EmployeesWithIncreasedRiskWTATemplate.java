package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

public class EmployeesWithIncreasedRiskWTATemplate extends WTABaseRuleTemplate {

    private int belowAge;
    private int aboveAge;
    private boolean pregnant;
    private boolean restingTimeAllowed;
    private int restingTime;

    public int getBelowAge() {
        return belowAge;
    }

    public void setBelowAge(int belowAge) {
        this.belowAge = belowAge;
    }


    public int getAboveYear() {
        return aboveAge;
    }

    public void setAboveYear(int aboveYear) {
        this.aboveAge = aboveYear;
    }

    public boolean isPregnant() {
        return pregnant;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public boolean isRestingTimeAllowed() {
        return restingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        this.restingTimeAllowed = restingTimeAllowed;
    }

    public int getRestingTime() {
        return restingTime;
    }

    public void setRestingTime(int restingTime) {
        this.restingTime = restingTime;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public EmployeesWithIncreasedRiskWTATemplate() {
        wtaTemplateType = WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {

    }

    public EmployeesWithIncreasedRiskWTATemplate(String name, boolean disabled, String description, int belowYear, int aboveYear, boolean pregnant) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.belowAge = belowYear;
        this.aboveAge = aboveYear;
        this.pregnant = pregnant;
    }
}
