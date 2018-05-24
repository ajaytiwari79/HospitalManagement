package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.TimeBankWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class TimeBankWrapper implements RuleTemplateWrapper{

    private RuleTemplateSpecificInfo infoWrapper;


    private TimeBankWTATemplate wtaTemplate;

    public TimeBankWrapper(TimeBankWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        return "";
    }
}
