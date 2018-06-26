package com.kairos.activity.client.counter;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ModuleCounterDTO {
    private BigInteger id;
    private BigInteger counterId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }
}
