package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

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
