package com.kairos.activity.spec;


import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by vipul on 8/2/18.
 */
public class WTARulesSpecification extends AbstractSpecification<ShiftWithActivityDTO> {
    Logger logger = LoggerFactory.getLogger(WTARulesSpecification.class);
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private List<WTABaseRuleTemplate> ruleTemplates;


    public WTARulesSpecification(RuleTemplateSpecificInfo ruleTemplateSpecificInfo, List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
        this.ruleTemplates = ruleTemplates;
    }



    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        ruleTemplates.forEach(ruleTemplateWrapper -> {
            ruleTemplateWrapper.isSatisfied(ruleTemplateSpecificInfo);
        });
        return true;
    }

}
