package com.kairos.persistence.model.user.expertise.response;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.util.List;

@QueryResult
public class ExpertisePlannedTimeQueryResult {
    public BigInteger includedPlannedTime;
    public BigInteger excludedPlannedTime;
    public List<EmploymentType> employmentTypes;

    public ExpertisePlannedTimeQueryResult() {
        //
    }

    public BigInteger getIncludedPlannedTime() {
        return includedPlannedTime;
    }

    public void setIncludedPlannedTime(BigInteger includedPlannedTime) {
        this.includedPlannedTime = includedPlannedTime;
    }

    public BigInteger getExcludedPlannedTime() {
        return excludedPlannedTime;
    }

    public void setExcludedPlannedTime(BigInteger excludedPlannedTime) {
        this.excludedPlannedTime = excludedPlannedTime;
    }

    public List<EmploymentType> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentType> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
