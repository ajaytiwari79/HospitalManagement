package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.organization.group.GroupWrapper;
import com.kairos.enums.OrganizationLevel;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by oodles on 30/11/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class OrganizationWrapper {

    private Long id;
    private String name;
    private String type;
    private boolean IsExpanded = true;
    private List<OrganizationWrapper> children;
    private List<GroupWrapper> groups;
    private boolean isKairosHub;
    private OrganizationLevel organizationLevel;

    public boolean isKairosHub() {
        return isKairosHub;
    }

    public void setKairosHub(boolean kairosHub) {
        isKairosHub = kairosHub;
    }

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

    public OrganizationLevel getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(OrganizationLevel organizationLevel) {
        this.organizationLevel = organizationLevel;
    }
}
