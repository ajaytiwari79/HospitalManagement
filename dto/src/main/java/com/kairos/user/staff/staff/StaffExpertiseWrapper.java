package com.kairos.user.staff.staff;

import java.util.Set;

public class StaffExpertiseWrapper {
    private Long staffId;
    private Set<Long> expertiseIds;
    private Long employmentTypeId;

    public StaffExpertiseWrapper() {
        //Default Constructor
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Set<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(Set<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }
}
