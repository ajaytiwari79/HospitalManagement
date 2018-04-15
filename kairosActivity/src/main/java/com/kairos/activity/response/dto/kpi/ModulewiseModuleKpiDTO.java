package com.kairos.activity.response.dto.kpi;

import java.util.ArrayList;
import java.util.List;

public class ModulewiseModuleKpiDTO {
    private String moduleId;
    private List<ModulewiseKpiDTO> modulewiseKpiDTOs = new ArrayList<>();

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<ModulewiseKpiDTO> getModulewiseKpiDTOs() {
        return modulewiseKpiDTOs;
    }

    public void setModulewiseKpiDTOs(List<ModulewiseKpiDTO> modulewiseKpiDTOs) {
        this.modulewiseKpiDTOs = modulewiseKpiDTOs;
    }
}
