package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 23/7/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTADistributionDTO {
    private BigInteger id;
    private String name;
    private int minutes;

}
