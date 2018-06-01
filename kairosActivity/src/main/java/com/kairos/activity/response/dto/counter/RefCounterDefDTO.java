package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.enums.counter.CounterType;

import java.math.BigInteger;

public class RefCounterDefDTO {

    private BigInteger id;
    private CounterType counterType;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public CounterType getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterType counterType) {
        this.counterType = counterType;
    }
}
