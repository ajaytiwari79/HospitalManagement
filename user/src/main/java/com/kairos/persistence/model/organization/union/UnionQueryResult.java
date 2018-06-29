package com.kairos.persistence.model.organization.union;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by pavan on 15/3/18.
 */
@QueryResult
public class UnionQueryResult {
    private Long id;
    private String name;

    public UnionQueryResult() {
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
}
