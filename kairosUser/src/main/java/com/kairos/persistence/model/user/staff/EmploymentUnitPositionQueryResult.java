package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 16/4/18.
 */

@QueryResult
public class EmploymentUnitPositionQueryResult {

Long unitPositionMinStartDate;
Long employmentEndDate;

    public Long getUnitPositionMinStartDate() {
        return unitPositionMinStartDate;
    }

    public void setUnitPositionMinStartDate(Long unitPositionMinStartDate) {
        this.unitPositionMinStartDate = unitPositionMinStartDate;
    }

    public Long getEmploymentEndDate() {
        return employmentEndDate;
    }

    public void setEmploymentEndDate(Long employmentEndDate) {
        this.employmentEndDate = employmentEndDate;
    }
}
