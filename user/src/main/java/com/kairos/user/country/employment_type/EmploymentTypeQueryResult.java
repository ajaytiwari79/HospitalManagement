package com.kairos.user.country.employment_type;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class EmploymentTypeQueryResult {
    private Long id;
    private String name;
    private List<EmploymentType> employmentType;

    public EmploymentTypeQueryResult() {

        // dc
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

