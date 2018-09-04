package com.kairos.user.access_permission;
/*
 *Created By Pavan on 31/8/18
 *
 */

import java.util.List;

public class StaffAccessGroupDTO {
    private Long staffId;
    private Long countryId;
    private Boolean isCountryAdmin;
    private List<Long> accessGroupIds;

    public StaffAccessGroupDTO() {
        //Default Constructor
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Boolean getCountryAdmin() {
        return isCountryAdmin;
    }

    public void setCountryAdmin(Boolean countryAdmin) {
        isCountryAdmin = countryAdmin;
    }

    public List<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(List<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }
}
