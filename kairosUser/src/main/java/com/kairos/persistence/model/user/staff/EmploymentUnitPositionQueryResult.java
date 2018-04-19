package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 16/4/18.
 */

@QueryResult
public class EmploymentUnitPositionQueryResult {

Long unitPositionMinStartDateMillis;
Long employmentEndDateMillis;

    public Long getUnitPositionMinStartDateMillis() {
        return unitPositionMinStartDateMillis;
    }

    public void setUnitPositionMinStartDateMillis(Long unitPositionMinStartDateMillis) {
        this.unitPositionMinStartDateMillis = unitPositionMinStartDateMillis;
    }

    public Long getEmploymentEndDateMillis() {
        return employmentEndDateMillis;
    }

    public void setEmploymentEndDateMillis(Long employmentEndDateMillis) {
        this.employmentEndDateMillis = employmentEndDateMillis;
    }
}
