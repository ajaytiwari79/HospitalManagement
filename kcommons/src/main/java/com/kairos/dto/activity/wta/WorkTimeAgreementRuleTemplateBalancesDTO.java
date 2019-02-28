package com.kairos.dto.activity.wta;

import java.util.List;

public class WorkTimeAgreementRuleTemplateBalancesDTO {

    private String name;
    private String timeTypeColor;
    private List<IntervalBalance> intervalBalances;

    public WorkTimeAgreementRuleTemplateBalancesDTO() {
    }

    public WorkTimeAgreementRuleTemplateBalancesDTO(String name, String timeTypeColor, List<IntervalBalance> intervalBalances) {
        this.name = name;
        this.timeTypeColor = timeTypeColor;
        this.intervalBalances = intervalBalances;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
