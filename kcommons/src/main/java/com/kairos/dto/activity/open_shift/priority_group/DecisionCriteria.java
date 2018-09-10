package com.kairos.dto.activity.open_shift.priority_group;

import java.math.BigInteger;
import java.util.Set;

public class DecisionCriteria {

    private Set<BigInteger> counterIds;
    public DecisionCriteria() {
        //Default Constructor
    }

    public Set<BigInteger> getCounterIds() {
        return counterIds;
    }

    public void setCounterIds(Set<BigInteger> counterIds) {
        this.counterIds = counterIds;
    }
}
