package com.kairos.dto.activity.pay_out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayOutPerShiftCTADistributionDTO {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;

}
