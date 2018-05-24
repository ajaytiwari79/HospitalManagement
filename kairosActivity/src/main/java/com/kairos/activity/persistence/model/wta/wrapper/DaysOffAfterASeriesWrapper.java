package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.DaysOffAfterASeriesWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DaysOffAfterASeriesWrapper implements RuleTemplateWrapper{


    private DaysOffAfterASeriesWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    @Override
    public String isSatisfied() {
        return "";
    }
}