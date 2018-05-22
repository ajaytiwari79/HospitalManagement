package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.DaysOffAfterASeriesWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DaysOffAfterASeriesWrapper implements RuleTemplateWrapper{


    private DaysOffAfterASeriesWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }
}