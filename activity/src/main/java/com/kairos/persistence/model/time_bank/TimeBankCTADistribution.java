package com.kairos.persistence.model.time_bank;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;

public class TimeBankCTADistribution {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;

    public TimeBankCTADistribution(String ctaName, int minutes, BigInteger ctaRuleTemplateId) {
        this.ctaName = ctaName;
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }

    public TimeBankCTADistribution() {
    }

    public TimeBankCTADistribution(BigInteger ctaRuleTemplateId, int minutes) {
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

        TimeBankCTADistribution that = (TimeBankCTADistribution) o;

        return new EqualsBuilder()
                .append(ctaName, that.ctaName)
                .append(ctaRuleTemplateId, that.ctaRuleTemplateId)
                .isEquals();
    }
    
}
