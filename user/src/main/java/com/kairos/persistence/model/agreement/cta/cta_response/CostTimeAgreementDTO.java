package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.persistence.model.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.agreement.cta.RuleTemplate;

import java.util.List;

/**
 * Created by pavan on 13/4/18.
 */
public class CostTimeAgreementDTO {
    private CostTimeAgreement cta;
    private List<RuleTemplate> ruleTemplates;

    public CostTimeAgreementDTO() {
        //Default Constructor
    }

    public CostTimeAgreement getCta() {
        return cta;
    }

    public void setCta(CostTimeAgreement cta) {
        this.cta = cta;
    }

    public List<RuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public CostTimeAgreementDTO(CostTimeAgreement cta, List<RuleTemplate> ruleTemplates) {
        this.cta = cta;
        this.ruleTemplates = ruleTemplates;
    }
}
