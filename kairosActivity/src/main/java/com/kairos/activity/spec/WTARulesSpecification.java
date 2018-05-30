package com.kairos.activity.spec;

import com.kairos.activity.persistence.model.activity.Activity;


import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by vipul on 8/2/18.
 */
public class WTARulesSpecification extends AbstractSpecification<ShiftWithActivityDTO> {
    Logger logger = LoggerFactory.getLogger(WTARulesSpecification.class);
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private List<WTABaseRuleTemplate> ruleTemplateWrappers;


    public WTARulesSpecification(RuleTemplateSpecificInfo ruleTemplateSpecificInfo, List<WTABaseRuleTemplate> ruleTemplateWrappers) {
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
        this.ruleTemplateWrappers = ruleTemplateWrappers;
    }



    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift,ExceptionService exceptionService) {
        ruleTemplateWrappers.forEach(ruleTemplateWrapper -> {
            ruleTemplateWrapper.isSatisfied(ruleTemplateSpecificInfo);
        });

        return true;
    }

}
