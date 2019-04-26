package com.planner.domain.query_results.employment;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class EmploymentQueryResult {
    public Long[] getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long[] employmentId) {
        this.employmentId = employmentId;
    }

    private Long[] employmentId;

}
