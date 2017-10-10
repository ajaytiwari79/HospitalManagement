package com.kairos.persistence.model.user.access_permission;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/10/17.
 */
public class AccessPageDTO {

    @NotNull(message = "error.name.notnull")
    private String name;
    private boolean module;
    private Long parentTabId;

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
}
