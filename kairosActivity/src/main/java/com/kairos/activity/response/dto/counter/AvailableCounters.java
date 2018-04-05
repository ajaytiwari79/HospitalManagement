package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.model.counter.CounterDefinition;

import java.util.List;

public class AvailableCounters {

    Long moduleId;
    List<CounterDefinition> counterDefinitions;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public List<CounterDefinition> getCounterDefinitions() {
        return counterDefinitions;
    }

    public void setCounterDefinitions(List<CounterDefinition> counterDefinitions) {
        this.counterDefinitions = counterDefinitions;
    }
}
