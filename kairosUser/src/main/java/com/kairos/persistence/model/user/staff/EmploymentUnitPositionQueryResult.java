package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 16/4/18.
 */

@QueryResult
public class EmploymentUnitPositionQueryResult {

Long earliestUnitPositionStartDateMillis;
Long employmentEndDateMillis;

    public Long getEarliestUnitPositionStartDateMillis() {
        return earliestUnitPositionStartDateMillis;
    }

    public void setEarliestUnitPositionStartDateMillis(Long earliestUnitPositionStartDateMillis) {
        this.earliestUnitPositionStartDateMillis = earliestUnitPositionStartDateMillis;
    }

    public Long getEmploymentEndDateMillis() {
        return employmentEndDateMillis;
    }

    public void setEmploymentEndDateMillis(Long employmentEndDateMillis) {
        this.employmentEndDateMillis = employmentEndDateMillis;
    }
}
