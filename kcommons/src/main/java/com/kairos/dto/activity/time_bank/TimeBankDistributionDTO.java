package com.kairos.dto.activity.time_bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeBankDistributionDTO {

    //cta ruletemplate based distributions
    private String ctaName;
    private BigInteger ctaRuleTemplateId;
    private LocalDate ctaDate;
    private int minutes;

}
