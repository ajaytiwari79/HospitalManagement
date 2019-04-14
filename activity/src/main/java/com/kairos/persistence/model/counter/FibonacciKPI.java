package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.util.List;

public class FibonacciKPI extends MongoBaseEntity {
    private String name;
    private String description;
    private List<FibinacciKPIConfig> fibinacciKPIConfigs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FibinacciKPIConfig> getFibinacciKPIConfigs() {
        return fibinacciKPIConfigs;
    }

    public void setFibinacciKPIConfigs(List<FibinacciKPIConfig> fibinacciKPIConfigs) {
        this.fibinacciKPIConfigs = fibinacciKPIConfigs;
    }
}
