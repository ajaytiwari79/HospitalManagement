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
    private boolean isSelected;
    private boolean isModule;
    private boolean isRead;
    private boolean isWrite;

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    public boolean isRead() {

        return isRead;
    }

    public boolean isWrite() {
        return isWrite;
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

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isModule() {
        return isModule;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setModule(boolean module) {
        isModule = module;
    }
}
