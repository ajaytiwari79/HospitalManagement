package com.kairos.dto.activity.wta;

import java.util.List;

public class WorkTimeAgreementBalance {

    private List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances;

    public List<WorkTimeAgreementRuleTemplateBalancesDTO> getWorkTimeAgreementRuleTemplateBalances() {
        return workTimeAgreementRuleTemplateBalances;
    }

    public void setWorkTimeAgreementRuleTemplateBalances(List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances) {
        this.workTimeAgreementRuleTemplateBalances = workTimeAgreementRuleTemplateBalances;
    }
}
