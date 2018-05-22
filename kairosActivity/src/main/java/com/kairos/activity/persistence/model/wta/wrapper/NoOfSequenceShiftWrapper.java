package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.NoOfSequenceShiftWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class NoOfSequenceShiftWrapper implements RuleTemplateWrapper{


    private NoOfSequenceShiftWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }
}
