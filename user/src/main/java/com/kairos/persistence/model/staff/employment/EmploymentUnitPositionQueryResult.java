package com.kairos.persistence.model.staff.employment;


import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by yatharth on 16/4/18.
 */

@QueryResult
public class EmploymentUnitPositionQueryResult {

private Long earliestUnitPositionStartDateMillis;
private Long employmentEndDateMillis;

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
