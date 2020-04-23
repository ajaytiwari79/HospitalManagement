package com.kairos.shiftplanning.domain.staff;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PayoutDistribution {

    private String ctaName;
    private BigInteger ctaRuleTemplateId;
    private int minutes;

}
