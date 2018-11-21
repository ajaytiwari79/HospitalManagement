package com.kairos.persistence.model.user.expertise.Response;

import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
@QueryResult
public class ExpertiseLocationStaffQueryResult {
    private Long expertiseId;
    private Location location;
    private Map<String,Object> staff; // Due to limitation its map We will remove later-->Can not set com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO field com.kairos.persistence.model.user.expertise.Response.ExpertiseLocationStaffQueryResult.staff to java.util.Collections$UnmodifiableMap


    public ExpertiseLocationStaffQueryResult() {
        // DC
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, Object> getStaff() {
        return staff;
    }

    public void setStaff(Map<String, Object> staff) {
        this.staff = staff;
    }
}
