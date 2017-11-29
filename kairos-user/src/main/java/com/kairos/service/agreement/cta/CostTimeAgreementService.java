package com.kairos.service.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateType;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.user.agreement.cta.CTARuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.auth.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional
@Service
public class CostTimeAgreementService extends UserBaseService {
    private Logger logger= LoggerFactory.getLogger(CostTimeAgreementService.class);

    private @Autowired UserService userService;
    private @Autowired RuleTemplateCategoryGraphRepository  ruleTemplateCategoryGraphRepository;
    private @Autowired CTARuleTemplateGraphRepository ctaRuleTemplateGraphRepository;

public void createDefaultCtaRuleTemplate(){
RuleTemplateCategory category=ruleTemplateCategoryGraphRepository
        .findByNameAndRuleTemplateCategoryType("NONE", RuleTemplateCategoryType.CTA);
    if(category!=null){
        Arrays.stream(CTARuleTemplateType.values()).parallel().forEach(cTARuleTemplate->{
            CTARuleTemplate ctaRuleTemplate=createRuleTemplate(cTARuleTemplate);
            category.addRuleTemplate(ctaRuleTemplate);
        });

        this.save(category);
    }else{
        logger.info("default CTARuleTemplateCategory is not exist");
    }

}

private CTARuleTemplate createRuleTemplate(CTARuleTemplateType ctaRuleTemplateType){
    CTARuleTemplate ctaRuleTemplate=null;
    switch (ctaRuleTemplateType){
        case RULE_TEMPLATE_1:
             ctaRuleTemplate=new CTARuleTemplate("Working Evening Shifts",
                    "CTA rule for evening shift, from 17-23 o'clock.  For this organization/unit this is payroll type '210:  Evening compensation'",
                     CTARuleTemplateType.RULE_TEMPLATE_1,"210:  Evening compensation","xyz");
                break;
        case RULE_TEMPLATE_2:

             ctaRuleTemplate=new CTARuleTemplate("Working Night Shifts",
                    "CTA rule for night shift, from 23-07 o. clock.  For this organization/unit this is payroll type “212:  Night compensation”",
                     CTARuleTemplateType.RULE_TEMPLATE_2,"212:  Night compensation","xyz");
            break;
        case RULE_TEMPLATE_3:

             ctaRuleTemplate=new CTARuleTemplate("Working On a Saturday",
                     "CTA rule for Saturdays shift, from 08-24 o. clock. For this organization/unit this is payroll type " +
                             "“214:  Saturday compensation”. If you are working from 00-07 on Saturday, you only gets evening " +
                             "compensation", CTARuleTemplateType.RULE_TEMPLATE_3,
                     "214:  Saturday compensation","xyz");
            break;
        case RULE_TEMPLATE_4:
            ctaRuleTemplate=new CTARuleTemplate("Working On a Sunday",
                    "CTA rule for Saturdays shift, from 00-24 o. clock. For this organization/unit this is " +
                            "payroll type “214:Saturday compensation”.All working time on Sundays gives compensation"
                    , CTARuleTemplateType.RULE_TEMPLATE_4,
                    "214:Saturday compensation","xyz");
            break;
        case RULE_TEMPLATE_5:
            ctaRuleTemplate=new CTARuleTemplate("Working On a Full Public Holiday",
                    "CTA rule for full public holiday shift, from 00-24 o. clock.  For this organization/unit this is " +
                            "payroll type “216:  public holiday compensation”. All working time on full PH gives " +
                            "compensation", CTARuleTemplateType.RULE_TEMPLATE_5,
                    "216:public holiday compensation","xyz");
            break;
        case RULE_TEMPLATE_6:
            ctaRuleTemplate=new CTARuleTemplate("Working On a Half Public Holiday",
                    "CTA rule for full public holiday shift, from 12-24 o. clock. For this organization/unit" +
                            " this is payroll type “218:  half public holiday compensation”.All working time on " +
                            "half PH gives compensation", CTARuleTemplateType.RULE_TEMPLATE_6,
                    "218: half public holiday compensation","xyz");
            break;
        case RULE_TEMPLATE_7:
            ctaRuleTemplate=new CTARuleTemplate("Working Overtime",
                    "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                      " 50% overtime compensation”.", CTARuleTemplateType.RULE_TEMPLATE_7,
                    "230:50% overtime compensation","xyz");
            break;
        case RULE_TEMPLATE_8:
            ctaRuleTemplate=new CTARuleTemplate("Working Extratime",
                    "CTA rule for extra time shift, from 00-24 o. clock.  For this organization/unit this is payroll type" +
                            " “250:  extratime compensation”. ", CTARuleTemplateType.RULE_TEMPLATE_8,
                    "250:  extratime compensation","xyz");
            break;
        case RULE_TEMPLATE_9:
            ctaRuleTemplate=new CTARuleTemplate("Late Notice Compensation",
                    "CTA rule for late notification on changes to working times.  If notice of change is done within 72 hours" +
                            " before start of working day, then staff is entitled to at compensation of 105 kroner",
                    CTARuleTemplateType.RULE_TEMPLATE_9,
                    "","xyz");
            break;
        case RULE_TEMPLATE_10:
            ctaRuleTemplate=new CTARuleTemplate("Extra Dutyfree Day For Each Public Holiday",
                    "CTA rule for each public holiday.  Whenever there is a public holiday staff are entitled to an" +
                            " extra day off, within 3 month or just compensated in the timebank.", CTARuleTemplateType.RULE_TEMPLATE_1,
                    "","xyz");
            break;
        default:
            throw new IllegalArgumentException("invalid template type");

    }
    return ctaRuleTemplate;
}
}