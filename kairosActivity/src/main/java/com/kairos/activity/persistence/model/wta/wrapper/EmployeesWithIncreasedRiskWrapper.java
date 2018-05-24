package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.EmployeesWithIncreasedRiskWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class EmployeesWithIncreasedRiskWrapper implements RuleTemplateWrapper{

    private EmployeesWithIncreasedRiskWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    @Override
    public String isSatisfied() {
        return "";
    }
}
