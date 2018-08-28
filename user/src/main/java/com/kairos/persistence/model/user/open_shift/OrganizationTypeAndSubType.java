package com.kairos.persistence.model.user.open_shift;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OrganizationTypeAndSubType {
    private Long id;
    private String name;
    private List<OrganizationType> children;
    private Long levelId;

    public OrganizationTypeAndSubType() {
        //Default Constructor
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

    public List<OrganizationType> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationType> children) {
        this.children = children;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}
