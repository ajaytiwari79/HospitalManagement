package com.kairos.rule_validator.activity;


import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        ruleTemplates.forEach(ruleTemplate -> ruleTemplate.validateRules(ruleTemplateSpecificInfo));
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        List<String> exceptionMessages = new ArrayList<>();/*
        for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
            String exceptionMessage = ;
            if(!exceptionMessage.isEmpty()){
                exceptionMessages.add("message.ruleTemplate.broken");
                exceptionMessages.add(exceptionMessage);
                break;
            }
        }*/
        return exceptionMessages;
    }

}
