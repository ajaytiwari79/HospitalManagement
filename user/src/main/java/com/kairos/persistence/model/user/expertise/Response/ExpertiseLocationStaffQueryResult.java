package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
@QueryResult
public class ExpertiseLocationStaffQueryResult {
    private Long expertiseId;
    private Long locationId;

    public ExpertiseLocationStaffQueryResult() {
        // DC
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
