package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleIdDTO {

    @NotBlank
    private String name;

    @NotBlank
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

    public ModuleIdDTO(String name, String moduleId, Boolean isModuleId, Boolean active) {
        this.name = name;
        this.moduleId = moduleId;
        this.isModuleId = isModuleId;
        this.active = active;
    }

    public ModuleIdDTO() {
    }
}
