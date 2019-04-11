package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.util.List;

public class CTARuletemplateBonus {

    private List<CTADistributionDTO> ctaDistributions;
    private long ctaBonusMinutes;

    public CTARuletemplateBonus() {
    }

    public CTARuletemplateBonus(List<CTADistributionDTO> ctaDistributions, long ctaBonusMinutes) {
        this.ctaDistributions = ctaDistributions;
        this.ctaBonusMinutes = ctaBonusMinutes;
    }

    public List<CTADistributionDTO> getCtaDistributions() {
        return ctaDistributions;
    }

    public void setCtaDistributions(List<CTADistributionDTO> ctaDistributions) {
        this.ctaDistributions = ctaDistributions;
    }

    public long getCtaBonusMinutes() {
        return ctaBonusMinutes;
    }

    public void setCtaBonusMinutes(long ctaBonusMinutes) {
        this.ctaBonusMinutes = ctaBonusMinutes;
    }
}
