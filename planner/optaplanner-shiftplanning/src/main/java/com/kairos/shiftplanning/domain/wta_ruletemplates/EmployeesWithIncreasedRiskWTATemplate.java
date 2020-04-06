package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.kairos.enums.wta.WTATemplateType;

public class EmployeesWithIncreasedRiskWTATemplate extends WTABaseRuleTemplate {

    private int belowAge;
    private int aboveAge;
    private boolean pregnant;
    private boolean restingTimeAllowed;
    private int restingTime;

    public EmployeesWithIncreasedRiskWTATemplate() {
        wtaTemplateType = WTATemplateType.EMPLOYEES_WITH_INCREASE_RISK;
    }

}
