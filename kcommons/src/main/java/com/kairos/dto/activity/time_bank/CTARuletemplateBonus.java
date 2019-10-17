package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CTARuletemplateBonus {

    private List<CTADistributionDTO> ctaDistributions;
    private long ctaBonusMinutes;
}
