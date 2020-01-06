package com.kairos.persistence.model.user.expertise.response;

import com.kairos.persistence.model.organization.union.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
@QueryResult
@Getter
@Setter
public class ExpertiseLocationStaffQueryResult {
    private Long expertiseId;
    private Location location;
    private Map<String,Object> staff; // Due to limitation its map We will remove later-->Can not set com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult field com.kairos.persistence.model.user.expertise.Response.ExpertiseLocationStaffQueryResult.staff to java.util.Collections$UnmodifiableMap
}
