package com.kairos.shiftplanning.domain.staff;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TimeBankDistribution {

    private String ctaName;
    private BigInteger ctaRuleTemplateId;
    private int minutes;

}
