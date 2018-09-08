package com.kairos.dto.user.staff.permission;

/**
 * Created by prabjot on 31/10/17.
 */
public class StaffTabPermission {

    private Long id;
    private String tabId;
    private boolean read;
    private boolean write;
    private String moduleId;
    private boolean active;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
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

    @Override
    public String toString() {
        return "StaffTabPermission{" +
                "id=" + id +
                ", tabId='" + tabId + '\'' +
                ", read=" + read +
                ", write=" + write +
                ", moduleId='" + moduleId + '\'' +
                ", active=" + active +
                '}';
    }
}
