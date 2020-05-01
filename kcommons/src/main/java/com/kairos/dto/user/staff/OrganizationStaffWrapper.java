package com.kairos.dto.user.staff;

import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vipul on 6/2/18.
 */
@Getter
@Setter
public class OrganizationStaffWrapper {
    private OrganizationDTO organization;
    private StaffDTO staff;
    private EmploymentDTO employment;
}
