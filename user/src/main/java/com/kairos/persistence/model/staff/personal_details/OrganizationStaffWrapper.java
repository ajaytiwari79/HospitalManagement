package com.kairos.persistence.model.staff.personal_details;

import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.employment.Employment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 6/2/18.
 */
@QueryResult
@Getter
@Setter
public class OrganizationStaffWrapper {
    private Unit unit;
    private Staff staff;
    private Employment employment;
}
