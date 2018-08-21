package com.kairos.activity.counter;

import com.kairos.activity.enums.counter.CounterSize;

import java.math.BigInteger;

public class KPIDTO {
    private BigInteger id;
    private String title;
    private boolean treatAsCounter;
    private CounterSize size;

    public KPIDTO(){

    }
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTreatAsCounter() {
        return treatAsCounter;
    }

    public void setTreatAsCounter(boolean treatAsCounter) {
        this.treatAsCounter = treatAsCounter;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }
}
