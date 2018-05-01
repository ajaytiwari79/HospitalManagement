package com.kairos.activity.persistence.model.pay_out;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class PayOutCTADistribution {

    private String ctaName;
    private int minutes;
    private Long ctaRuleTemplateId;
    private String payrollType;
    private String payrollSystem;

    public PayOutCTADistribution(String ctaName, int minutes, Long ctaRuleTemplateId, String payrollSystem, String payrollType) {
        this.ctaName = ctaName;
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
        this.payrollSystem = payrollSystem;
        this.payrollType = payrollType;
    }

    public PayOutCTADistribution() {
    }

    public PayOutCTADistribution(Long ctaRuleTemplateId, int minutes) {
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }

    public String getCtaName() {
        return ctaName;
    }

    public void setCtaName(String ctaName) {
        this.ctaName = ctaName;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Long getCtaRuleTemplateId() {
        return ctaRuleTemplateId;
    }

    public void setCtaRuleTemplateId(Long ctaRuleTemplateId) {
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PayOutCTADistribution that = (PayOutCTADistribution) o;

        return new EqualsBuilder()
                .append(ctaName, that.ctaName)
                .append(ctaRuleTemplateId, that.ctaRuleTemplateId)
                .isEquals();
    }

    public String getPayrollType() {
        return payrollType;
    }

    public void setPayrollType(String payrollType) {
        this.payrollType = payrollType;
    }

    public String getPayrollSystem() {
        return payrollSystem;
    }

    public void setPayrollSystem(String payrollSystem) {
        this.payrollSystem = payrollSystem;
    }
}
