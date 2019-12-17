package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@Getter
@Setter
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffPermissionRelatedDataQueryResult {


    private Long staffId;
    private List<Long> teamIds;
    private List<Long> employmentTypeIds;
    private List<Long> expertiseIds;
    private List<StaffStatusEnum> staffStatuses;
    private List<Long> tagIds;

}
