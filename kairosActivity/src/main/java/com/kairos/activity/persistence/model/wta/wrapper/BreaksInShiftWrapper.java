package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.BreaksInShiftWTATemplate; /**
 * @author pradeep
 * @date - 22/5/18
 */

public class BreaksInShiftWrapper implements RuleTemplateWrapper{

    private RuleTemplateSpecificInfo infoWrapper;
    private BreaksInShiftWTATemplate wtaTemplate;

    public BreaksInShiftWrapper(BreaksInShiftWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        return "";
    }
}