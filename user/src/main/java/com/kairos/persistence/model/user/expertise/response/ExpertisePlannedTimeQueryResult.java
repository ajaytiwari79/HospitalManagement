package com.kairos.persistence.model.user.expertise.response;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.util.List;

@QueryResult
@Getter
@Setter
public class ExpertisePlannedTimeQueryResult {
    private BigInteger includedPlannedTime;
    private BigInteger excludedPlannedTime;
    private List<EmploymentType> employmentTypes;
}
