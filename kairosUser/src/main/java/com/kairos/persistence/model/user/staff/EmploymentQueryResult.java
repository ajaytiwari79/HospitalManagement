package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 13/4/18.
 */

@QueryResult
public class EmploymentQueryResult {

    private Long id;
    private String name;
    private Long startDateMillis;
    private Long endDateMillis;

    public EmploymentQueryResult() {

    }
    public EmploymentQueryResult(Long id, Long startDateMillis, Long endDateMillis) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;

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

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }


}
