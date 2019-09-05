package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class AccessGroupStaffQueryResult {
    private OrganizationBaseEntity organization;
    private Long staffId;
    private List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup;
}


