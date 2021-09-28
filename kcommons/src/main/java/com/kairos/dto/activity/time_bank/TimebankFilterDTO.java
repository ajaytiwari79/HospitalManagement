package com.kairos.dto.activity.time_bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimebankFilterDTO {

    private Set<BigInteger> dayTypeIds;
    private Set<BigInteger> timeSoltIds;
}
