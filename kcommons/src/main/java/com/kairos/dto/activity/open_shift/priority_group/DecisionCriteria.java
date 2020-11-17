package com.kairos.dto.activity.open_shift.priority_group;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
public class DecisionCriteria {

    private Set<BigInteger> counterIds;
}
