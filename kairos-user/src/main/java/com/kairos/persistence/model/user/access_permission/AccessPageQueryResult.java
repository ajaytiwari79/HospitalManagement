package com.kairos.persistence.model.user.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 26/1/17.
 */
@QueryResult
public class AccessPageQueryResult {

    private long id;
    private String name;
    private boolean selected;
    private boolean module;
    private boolean read;
    private boolean write;
    private boolean active;
    private String moduleId;


    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    private List<AccessPageQueryResult> children = new ArrayList<>();

    public List<AccessPageQueryResult> getChildren() {
        return children;
    }

    public void setChildren(List<AccessPageQueryResult> children) {
        this.children = children;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
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
        return "AccessPageQueryResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", module=" + module +
                ", read=" + read +
                ", write=" + write +
                ", active=" + active +
                ", moduleId='" + moduleId + '\'' +
                ", children=" + children +
                '}';
    }
}
