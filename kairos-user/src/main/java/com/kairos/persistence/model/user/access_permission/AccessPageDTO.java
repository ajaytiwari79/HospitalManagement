package com.kairos.persistence.model.user.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/10/17.
 */
@QueryResult
public class AccessPageDTO {

    private Long id;
    @NotNull(message = "error.name.notnull")
    private String name;
    private boolean module;
    private Long parentTabId;
    private String moduleId;
    private Boolean active;


    public Long getParentTabId() {
        return parentTabId;
    }

    public void setParentTabId(Long parentTabId) {
        this.parentTabId = parentTabId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
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
}
