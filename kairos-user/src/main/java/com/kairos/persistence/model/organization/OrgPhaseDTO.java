package com.kairos.persistence.model.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 20/9/17.
 */
@QueryResult
public class OrgPhaseDTO {
    private String name;
    private String email;
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
