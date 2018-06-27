package com.kairos.user.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 26/2/18.
 */
@QueryResult
public class OrganizationBasicResponse {
    private Long id;
    private String name;

    public OrganizationBasicResponse() {
    }

    public OrganizationBasicResponse(Long id, String name) {
        this.id = id;
        this.name = name;
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
}
