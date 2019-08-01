package com.kairos.wrapper.staff;

import com.kairos.persistence.model.country.employment_type.EmploymentType;

import java.util.*;

public class StaffEmploymentTypeWrapper {
    private List<EmploymentType> employmentTypes= new ArrayList<>();
    private List<Map> staffList = new ArrayList<>();
    private Long loggedInStaffId;

    public StaffEmploymentTypeWrapper() {
        // DC
    }

    public List<EmploymentType> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentType> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Map> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Map> staffList) {
        this.staffList = staffList;
    }

    public Long getLoggedInStaffId() {
        return loggedInStaffId;
    }

    public void setLoggedInStaffId(Long loggedInStaffId) {
        this.loggedInStaffId = loggedInStaffId;
    }
}
