package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class TimeBankCTADistributionDTO {

    //cta ruletemplate based distributions
    private List<CTADistributionDTO> children;
    private long minutes;
    private String ctaName;
    private BigInteger ctaRuleTemplateId;
    private LocalDate ctaDate;

    public TimeBankCTADistributionDTO() {
    }

    public TimeBankCTADistributionDTO(String ctaName, int minutes, BigInteger ctaRuleTemplateId,LocalDate ctaDate) {
        this.ctaName = ctaName;
        this.minutes = minutes;
        this.ctaRuleTemplateId = ctaRuleTemplateId;
        this.ctaDate = ctaDate;
    }

    public TimeBankCTADistributionDTO(List<CTADistributionDTO> children, long minutes) {
        this.children = children;
        this.minutes = minutes;
    }

    public LocalDate getCtaDate() {
        return ctaDate;
    }

    public void setCtaDate(LocalDate ctaDate) {
        this.ctaDate = ctaDate;
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

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public List<CTADistributionDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CTADistributionDTO> children) {
        this.children = children;
    }
}
