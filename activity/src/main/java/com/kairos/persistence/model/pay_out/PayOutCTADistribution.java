package com.kairos.persistence.model.pay_out;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class PayOutCTADistribution {

    private String ctaName;
    private int minutes;
    private Long ctaRuleTemplateId;


    public PayOutCTADistribution() {
    }

    public PayOutCTADistribution(String ctaName, int minutes,Long ctaRuleTemplateId) {
        this.ctaName = ctaName;
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


}