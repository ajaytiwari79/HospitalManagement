package com.kairos.dto.activity.pay_out;

import com.kairos.dto.activity.time_bank.CTARuletemplateBonus;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.util.List;

public class PayOutCTADistributionDTO {

    //cta ruletemplate based distributions
    private List<CTADistributionDTO> scheduledCTADistributions;
    private CTARuletemplateBonus ctaRuletemplateBonus;
    private long plannedMinutesOfPayout;

    public PayOutCTADistributionDTO() {
    }

    public PayOutCTADistributionDTO(List<CTADistributionDTO> scheduledCTADistributions,CTARuletemplateBonus ctaRuletemplateBonus,long plannedMinutesOfPayout) {
        this.scheduledCTADistributions = scheduledCTADistributions;
        this.ctaRuletemplateBonus = ctaRuletemplateBonus;
        this.plannedMinutesOfPayout = plannedMinutesOfPayout;
    }

    public List<CTADistributionDTO> getScheduledCTADistributions() {
        return scheduledCTADistributions;
    }

    public void setScheduledCTADistributions(List<CTADistributionDTO> scheduledCTADistributions) {
        this.scheduledCTADistributions = scheduledCTADistributions;
    }

    public CTARuletemplateBonus getCtaRuletemplateBonus() {
        return ctaRuletemplateBonus;
    }

    public void setCtaRuletemplateBonus(CTARuletemplateBonus ctaRuletemplateBonus) {
        this.ctaRuletemplateBonus = ctaRuletemplateBonus;
    }

    public long getPlannedMinutesOfPayout() {
        return plannedMinutesOfPayout;
    }

    public void setPlannedMinutesOfPayout(long plannedMinutesOfPayout) {
        this.plannedMinutesOfPayout = plannedMinutesOfPayout;
    }
}
