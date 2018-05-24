package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.NoOfSequenceShiftWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class NoOfSequenceShiftWrapper implements RuleTemplateWrapper{


    private NoOfSequenceShiftWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    @Override
    public String isSatisfied() {
        return "";
    }
}
