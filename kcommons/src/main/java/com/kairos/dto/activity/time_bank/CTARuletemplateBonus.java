package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTARuletemplateBonus  implements Serializable {

    private List<CTADistributionDTO> ctaDistributions;
    private double ctaBonusMinutes;
}
