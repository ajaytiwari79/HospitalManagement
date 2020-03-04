package com.kairos.persistence.model.user.employment.query_result;

import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.employment.EmploymentLine;
import com.kairos.persistence.model.user.employment.EmploymentLineEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

@QueryResult
@Getter
@Setter
public class EmploymentSeniorityLevelQueryResult {
    private Long employmentId;
    private SeniorityLevel seniorityLevel;
    private EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip;
    private EmploymentType employmentType;
    private EmploymentLine employmentLine;
    private LocalDate expertiseEndDate;
}
