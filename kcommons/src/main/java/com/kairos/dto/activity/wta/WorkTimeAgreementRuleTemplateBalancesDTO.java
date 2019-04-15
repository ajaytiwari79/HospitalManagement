package com.kairos.dto.activity.wta;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;

import java.util.List;

public class WorkTimeAgreementRuleTemplateBalancesDTO {

    private String name;
    private String timeTypeColor;
    private List<IntervalBalance> intervalBalances;
    private CutOffIntervalUnit cutOffIntervalUnit;

    public WorkTimeAgreementRuleTemplateBalancesDTO() {
    }

    public WorkTimeAgreementRuleTemplateBalancesDTO(String name, String timeTypeColor, List<IntervalBalance> intervalBalances,CutOffIntervalUnit cutOffIntervalUnit) {
        this.name = name;
        this.timeTypeColor = timeTypeColor;
        this.intervalBalances = intervalBalances;
        this.cutOffIntervalUnit=cutOffIntervalUnit;
    }

    public CutOffIntervalUnit getCutOffIntervalUnit() { return cutOffIntervalUnit; }

    public void setCutOffIntervalUnit(CutOffIntervalUnit cutOffIntervalUnit) { this.cutOffIntervalUnit = cutOffIntervalUnit; }

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
