package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleIdDto {

    @NotNullOrEmpty
    private String name;

    @NotNullOrEmpty
    private String moduleId;

    private Boolean isModuleId ;

    private Boolean active;


    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(Boolean moduleId) {
        isModuleId = moduleId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleIdDto(String name, String moduleId, Boolean isModuleId, Boolean active) {
        this.name = name;
        this.moduleId = moduleId;
        this.isModuleId = isModuleId;
        this.active = active;
    }

    public ModuleIdDto() {
    }
}
