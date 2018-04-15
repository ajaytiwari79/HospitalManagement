package com.kairos.activity.response.dto.kpi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ModulewiseKpiGroupingDTO {
    private String moduleId;
    private List<BigInteger> kpiIds = new ArrayList<>();

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
