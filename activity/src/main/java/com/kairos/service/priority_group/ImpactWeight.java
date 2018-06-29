package com.kairos.service.priority_group;

public class ImpactWeight {

    private Integer timBankImpact;
    private Integer assignedOpenShiftImpact;

    public ImpactWeight() {

    }
    public ImpactWeight(Integer timeBankImpact,Integer assignedOpenShiftImpact) {
        this.timBankImpact = timeBankImpact;
        this.assignedOpenShiftImpact = assignedOpenShiftImpact;
    }
    public Integer getTimBankImpact() {
        return timBankImpact;
    }

    public void setTimBankImpact(Integer timBankImpact) {
        this.timBankImpact = timBankImpact;
    }

    public Integer getAssignedOpenShiftImpact() {
        return assignedOpenShiftImpact;
    }

    public void setAssignedOpenShiftImpact(Integer assignedOpenShiftImpact) {
        this.assignedOpenShiftImpact = assignedOpenShiftImpact;
    }

}
