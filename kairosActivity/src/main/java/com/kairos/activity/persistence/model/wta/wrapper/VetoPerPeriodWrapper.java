package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.VetoPerPeriodWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class VetoPerPeriodWrapper implements RuleTemplateWrapper{

    private RuleTemplateSpecificInfo infoWrapper;
    private VetoPerPeriodWTATemplate wtaTemplate;

    public VetoPerPeriodWrapper(VetoPerPeriodWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        return "";
    }
}
