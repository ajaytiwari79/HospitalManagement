package com.kairos.activity.counter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ManipulatableCounterIdsDTO {
    private List<BigInteger> counterIdsToRemove = new ArrayList<BigInteger>();
    private List<BigInteger> counterIdsToAdd = new ArrayList<BigInteger>();

    public List<BigInteger> getCounterIdsToRemove() {
        return counterIdsToRemove;
    }

    public void setCounterIdsToRemove(List<BigInteger> counterIdsToRemove) {
        this.counterIdsToRemove = counterIdsToRemove;
    }

    public List<BigInteger> getCounterIdsToAdd() {
        return counterIdsToAdd;
    }

    public void setCounterIdsToAdd(List<BigInteger> counterIdsToAdd) {
        this.counterIdsToAdd = counterIdsToAdd;
    }
}
