package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CounterModuleLink extends MongoBaseEntity {
    private String moduleId;
    private BigInteger counterDefinitionId;

    public BigInteger getCounterDefinitionId() {
        return counterDefinitionId;
    }

    public void setCounterDefinitionId(BigInteger counterDefinitionId) {
        this.counterDefinitionId = counterDefinitionId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
