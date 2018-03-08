package com.kairos.persistence.model.user.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 5/3/18.
 */
@QueryResult
public class AccessGroupQueryResult {

    private long id;
    private String name;
    private boolean deleted;
    private boolean typeOfTaskGiver;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isTypeOfTaskGiver() {
        return typeOfTaskGiver;
    }

    public void setTypeOfTaskGiver(boolean typeOfTaskGiver) {
        this.typeOfTaskGiver = typeOfTaskGiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
