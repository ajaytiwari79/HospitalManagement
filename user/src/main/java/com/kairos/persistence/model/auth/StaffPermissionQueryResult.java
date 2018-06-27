package com.kairos.persistence.model.auth;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 31/10/17.
 */
@QueryResult
public class StaffPermissionQueryResult {

    private Long id; // id of access page
    private Long accessGroupId;
    private String name;
    private boolean read;
    private boolean write;
    private String moduleId;
    private List<Map<String,Object>> tabPermissions;
    private boolean module;
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public List<Map<String, Object>> getTabPermissions() {
        return tabPermissions;
    }

    public void setTabPermissions(List<Map<String, Object>> tabPermissions) {
        this.tabPermissions = tabPermissions;
    }

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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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


    @Override
    public String toString() {
        return "StaffPermissionQueryResult{" +
                "id=" + id +
                ", accessGroupId=" + accessGroupId +
                ", name='" + name + '\'' +
                ", read=" + read +
                ", write=" + write +
                ", moduleId='" + moduleId + '\'' +
                ", tabPermissions=" + tabPermissions +
                ", module=" + module +
                ", active=" + active +
                '}';
    }
}
