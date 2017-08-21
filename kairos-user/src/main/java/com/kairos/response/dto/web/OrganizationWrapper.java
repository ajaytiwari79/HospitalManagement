package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by oodles on 30/11/16.
 */
public class OrganizationWrapper {

    private Long id;
    private String name;
    private String type;
    private boolean IsExpanded = true;
    private List<OrganizationWrapper> children;
    private List<GroupWrapper> groups;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExpanded() {
        return IsExpanded;
    }

    public void setExpanded(boolean expanded) {
        IsExpanded = expanded;
    }

    public List<OrganizationWrapper> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationWrapper> children) {
        this.children = children;
    }

    public List<GroupWrapper> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupWrapper> groups) {
        this.groups = groups;
    }
}
