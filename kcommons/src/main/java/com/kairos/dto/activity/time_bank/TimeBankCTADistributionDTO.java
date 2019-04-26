package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;

import java.util.List;

public class TimeBankCTADistributionDTO {

    //cta ruletemplate based distributions
    private List<CTADistributionDTO> scheduledCTADistributions;
    private CTARuletemplateBonus ctaRuletemplateBonus;
    private long plannedMinutesOfTimebank;



    public TimeBankCTADistributionDTO() {
    }

    public TimeBankCTADistributionDTO(List<CTADistributionDTO> scheduledCTADistributions,CTARuletemplateBonus ctaRuletemplateBonus,long plannedMinutesOfTimebank) {
        this.scheduledCTADistributions = scheduledCTADistributions;
        this.ctaRuletemplateBonus = ctaRuletemplateBonus;
        this.plannedMinutesOfTimebank = plannedMinutesOfTimebank;
    }

    public CTARuletemplateBonus getCtaRuletemplateBonus() {
        return ctaRuletemplateBonus;
    }

    public void setCtaRuletemplateBonus(CTARuletemplateBonus ctaRuletemplateBonus) {
        this.ctaRuletemplateBonus = ctaRuletemplateBonus;
    }

    public List<CTADistributionDTO> getScheduledCTADistributions() {
        return scheduledCTADistributions;
    }

    public void setScheduledCTADistributions(List<CTADistributionDTO> scheduledCTADistributions) {
        this.scheduledCTADistributions = scheduledCTADistributions;
    }

    public long getPlannedMinutesOfTimebank() {
        return plannedMinutesOfTimebank;
    }

    public void setPlannedMinutesOfTimebank(long plannedMinutesOfTimebank) {
        this.plannedMinutesOfTimebank = plannedMinutesOfTimebank;
    }
}
