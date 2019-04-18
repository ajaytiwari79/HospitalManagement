package com.kairos.dto.activity.time_bank;

import java.math.BigInteger;
import java.time.LocalDate;

public class TimeBankDistributionDTO {

    //cta ruletemplate based distributions
    private String ctaName;
    private BigInteger ctaRuleTemplateId;
    private LocalDate ctaDate;
    private int minutes;

    public TimeBankDistributionDTO() {
    }

    public TimeBankDistributionDTO(String ctaName, BigInteger ctaRuleTemplateId, LocalDate ctaDate, int minutes) {
        this.ctaName = ctaName;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
        this.ctaDate = ctaDate;
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getCtaName() {
        return ctaName;
    }

    public void setCtaName(String ctaName) {
        this.ctaName = ctaName;
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
}
