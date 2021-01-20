package com.kairos.rule_validator.activity;


import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.rule_validator.RuleExecutionType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

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
    public void validateRules(ShiftWithActivityDTO shift, RuleExecutionType ruleExecutionType) {
        //TODO Don't Remove this code just commented for some temp fixes
//        for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
//            if (RuleExecutionType.COVER_SHIFT.equals(ruleExecutionType) && isCollectionNotEmpty(this.ruleTemplateSpecificInfo.getViolatedRules().getWorkTimeAgreements())) {
//                break;
//            }
//            ruleTemplate.validateRules(ruleTemplateSpecificInfo);
//        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return new ArrayList<>();
    }

}
