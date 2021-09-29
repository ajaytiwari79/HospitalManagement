package com.kairos.dto.activity.pay_out;

import com.kairos.dto.activity.time_bank.CTARuletemplateBonus;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOutCTADistributionDTO {

    //cta ruletemplate based distributions
    private List<CTADistributionDTO> scheduledCTADistributions;
    private CTARuletemplateBonus ctaRuletemplateBonus;
    private double plannedMinutesOfPayout;

}
