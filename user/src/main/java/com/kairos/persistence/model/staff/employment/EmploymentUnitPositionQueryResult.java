package com.kairos.persistence.model.staff.employment;


import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by yatharth on 16/4/18.
 */

@QueryResult
public class EmploymentUnitPositionQueryResult {

private LocalDate earliestUnitPositionStartDateMillis;
private LocalDate employmentEndDateMillis;

    public LocalDate getEarliestUnitPositionStartDateMillis() {
        return earliestUnitPositionStartDateMillis;
    }

    public void setEarliestUnitPositionStartDateMillis(LocalDate earliestUnitPositionStartDateMillis) {
        this.earliestUnitPositionStartDateMillis = earliestUnitPositionStartDateMillis;
    }

    public LocalDate getEmploymentEndDateMillis() {
        return employmentEndDateMillis;
    }

    public void setEmploymentEndDateMillis(LocalDate employmentEndDateMillis) {
        this.employmentEndDateMillis = employmentEndDateMillis;
    }
}
