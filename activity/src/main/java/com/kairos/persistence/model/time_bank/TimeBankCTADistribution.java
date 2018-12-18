package com.kairos.persistence.model.time_bank;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;
import java.time.LocalDate;

public class TimeBankCTADistribution {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;
    private LocalDate ctaDate;

    public TimeBankCTADistribution(String ctaName, int minutes, BigInteger ctaRuleTemplateId) {
        this.ctaName = ctaName;
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
    }





    public TimeBankCTADistribution() {
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


    public LocalDate getCtaDate() {
        return ctaDate;
    }

    public void setCtaDate(LocalDate ctaDate) {
        this.ctaDate = ctaDate;
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
