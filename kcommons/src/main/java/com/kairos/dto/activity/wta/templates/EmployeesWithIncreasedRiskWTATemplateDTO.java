package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;

public class EmployeesWithIncreasedRiskWTATemplateDTO extends WTABaseRuleTemplateDTO {


    private int belowAge;
    private int aboveAge;
    private boolean pregnant;
    private boolean restingTimeAllowed;
    private int restingTime;

    public int getBelowYear() {
        return belowAge;
    }

    public void setBelowYear(int belowYear) {
        this.belowAge = belowYear;
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

    public EmployeesWithIncreasedRiskWTATemplateDTO() {
        wtaTemplateType=WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }
}
