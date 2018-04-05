package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.model.counter.CounterDefinition;

import java.math.BigInteger;

public class CounterModuleLinkDTO {
    private BigInteger id;
    private String moduleId;
    private CounterDefinition counterDefinition;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public CounterDefinition getCounterDefinition() {
        return counterDefinition;
    }

    public void setCounterDefinition(CounterDefinition counterDefinition) {
        this.counterDefinition = counterDefinition;
    }
}
