package com.kairos.dto.activity.pay_out;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class PayOutPerShiftCTADistributionDTO {

    private String ctaName;
    private int minutes;
    private BigInteger ctaRuleTemplateId;

}
