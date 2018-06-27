package com.kairos.user.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 16/11/17.
 */
@QueryResult
public class OrganizationServiceQueryResult {
    private Long id;
    private String name;
    private String customName;
    private String description;

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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
