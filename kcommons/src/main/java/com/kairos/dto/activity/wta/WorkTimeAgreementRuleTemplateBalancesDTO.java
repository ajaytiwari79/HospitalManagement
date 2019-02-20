package com.kairos.dto.activity.wta;

import java.util.List;

public class WorkTimeAgreementRuleTemplateBalancesDTO {

    private String wtaRuleTemplateName;
    private String timeTypeColor;
    private List<IntervalBalance> intervalBalances;

    public String getWtaRuleTemplateName() {
        return wtaRuleTemplateName;
    }

    public void setWtaRuleTemplateName(String wtaRuleTemplateName) {
        this.wtaRuleTemplateName = wtaRuleTemplateName;
    }

    public String getTimeTypeColor() {
        return timeTypeColor;
    }

    public void setTimeTypeColor(String timeTypeColor) {
        this.timeTypeColor = timeTypeColor;
    }

    public List<IntervalBalance> getIntervalBalances() {
        return intervalBalances;
    }

    public void setIntervalBalances(List<IntervalBalance> intervalBalances) {
        this.intervalBalances = intervalBalances;
    }
}
