package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.EmployeesWithIncreasedRiskWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class EmployeesWithIncreasedRiskWrapper implements RuleTemplateWrapper{

    private EmployeesWithIncreasedRiskWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }
}
