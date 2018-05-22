package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.TimeBankWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class TimeBankWrapper implements RuleTemplateWrapper{

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;


    private TimeBankWTATemplate wtaTemplate;

    @Override
    public boolean isSatisfied() {
        return false;
    }
}
