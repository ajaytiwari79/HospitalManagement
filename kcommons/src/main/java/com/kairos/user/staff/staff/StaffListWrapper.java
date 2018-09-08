package com.kairos.user.staff.staff;


import java.util.List;
import java.util.Map;

public class StaffListWrapper {
    private List<Map> staffList;
    private List<Map> employmentTypes;

    public StaffListWrapper() {
        //dc
    }

    public List<Map> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Map> staffList) {
        this.staffList = staffList;
    }

    public List<Map> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Map> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public StaffListWrapper(List<Map> staffList, List<Map> employmentTypes) {
        this.staffList = staffList;
        this.employmentTypes = employmentTypes;
    }
}
