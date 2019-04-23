package com.kairos.persistence.model.organization.union;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 13/2/18.
 */
@QueryResult
public class UnionResponseDTO {
    private Long id;
    private String name;

    public UnionResponseDTO() {
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
