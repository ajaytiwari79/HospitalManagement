package com.kairos.dto.activity.pay_out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayOutPerShiftCTADistributionDTO implements Serializable {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;

}
