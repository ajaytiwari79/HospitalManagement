package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.ChildCareDaysCheckWTATemplate; /**
 * @author pradeep
 * @date - 22/5/18
 */

public class ChildCareDaysCheckWrapper implements RuleTemplateWrapper{

    private ChildCareDaysCheckWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public ChildCareDaysCheckWrapper(ChildCareDaysCheckWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        return "";
    }
}