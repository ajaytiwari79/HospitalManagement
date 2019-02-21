package com.kairos.persistence.model.pay_out;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.math.BigInteger;

public class PayOutPerShiftCTADistribution {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;


    public PayOutPerShiftCTADistribution() {
    }

    public PayOutPerShiftCTADistribution(String ctaName, int minutes, BigInteger ctaRuleTemplateId) {
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

    public BigInteger getCtaRuleTemplateId() {
        return ctaRuleTemplateId;
    }

    public void setCtaRuleTemplateId(BigInteger ctaRuleTemplateId) {
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PayOutPerShiftCTADistribution that = (PayOutPerShiftCTADistribution) o;

        return new EqualsBuilder()
                .append(ctaName, that.ctaName)
                .append(ctaRuleTemplateId, that.ctaRuleTemplateId)
                .isEquals();
    }


}