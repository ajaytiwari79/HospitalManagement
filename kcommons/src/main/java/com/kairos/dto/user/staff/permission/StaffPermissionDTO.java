package com.kairos.dto.user.staff.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by prabjot on 1/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffPermissionDTO {

    private Long id; // id of access page
    private Long accessGroupId;
    private String name;
    private boolean read;
    private boolean write;
    private String moduleId;
    private List<StaffTabPermission> tabPermissions;
    private boolean module;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<StaffTabPermission> getTabPermissions() {
        return tabPermissions;
    }

    public void setTabPermissions(List<StaffTabPermission> tabPermissions) {
        this.tabPermissions = tabPermissions;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
