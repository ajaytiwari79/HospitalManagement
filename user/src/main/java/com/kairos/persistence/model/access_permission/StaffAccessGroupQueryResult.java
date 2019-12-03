package com.kairos.persistence.model.access_permission;
/*
 *Created By Pavan on 30/8/18
 *
 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
public class StaffAccessGroupQueryResult {

    private Long staffId;
    private Long countryId;
    private boolean isCountryAdmin;
    private List<Long> accessGroupIds;
    private boolean staff;
    private boolean management;
    private Long unitId;
    private boolean hasPermission;
    private List<AccessGroup> accessGroups;
}
