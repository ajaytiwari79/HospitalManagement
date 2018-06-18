package com.kairos.activity.response.dto.counter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ModulewiseCounterGroupingDTO {
    private String moduleId;
    private List<BigInteger> counterIds = new ArrayList<>();

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<BigInteger> getCounterIds() {
        return counterIds;
    }

    public void setCounterIds(List<BigInteger> counterIds) {
        this.counterIds = counterIds;
    }
}
