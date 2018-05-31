package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

public class EmployeesWithIncreasedRiskWTATemplate extends WTABaseRuleTemplate{

 private int belowAge;
 private int aboveAge;
 private boolean pregnant;
 private boolean isRestingTimeAllowed;
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
        return isRestingTimeAllowed;
    }

    public void setRestingTimeAllowed(boolean restingTimeAllowed) {
        isRestingTimeAllowed = restingTimeAllowed;
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
        wtaTemplateType=WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }

    public EmployeesWithIncreasedRiskWTATemplate(String name, boolean disabled, String description, int belowYear, int aboveYear, boolean pregnant) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.belowAge = belowYear;
        this.aboveAge = aboveYear;
        this.pregnant = pregnant;
    }
}
