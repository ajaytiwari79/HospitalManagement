package com.kairos.persistence.model.staff.personal_details;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class StaffAndExpertiseIdsQueryResult {

    private Long id;
    private List<Long> expertiseIds;
}
