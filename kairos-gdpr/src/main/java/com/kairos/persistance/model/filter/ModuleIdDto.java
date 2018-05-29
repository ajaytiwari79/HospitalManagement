package com.kairos.persistance.model.filter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleIdDto {

    @NotNullOrEmpty
    private String name;

    private Boolean isModuleId;

    @NotNullOrEmpty
    private String moduleId;

    private Boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getModuleId() {
        return isModuleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setModuleId(Boolean moduleId) {
        isModuleId = moduleId;
    }


}
