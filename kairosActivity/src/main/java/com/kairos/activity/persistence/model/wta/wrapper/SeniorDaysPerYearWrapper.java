package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.SeniorDaysPerYearWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;

import java.util.List;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class SeniorDaysPerYearWrapper implements RuleTemplateWrapper{


    private SeniorDaysPerYearWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        DateTimeInterval dateTimeInterval =
        return true;
    }
}
