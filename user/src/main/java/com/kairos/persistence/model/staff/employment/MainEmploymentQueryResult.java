package com.kairos.persistence.model.staff.employment;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class MainEmploymentQueryResult {
    private Position position;
    private String organizationName;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
