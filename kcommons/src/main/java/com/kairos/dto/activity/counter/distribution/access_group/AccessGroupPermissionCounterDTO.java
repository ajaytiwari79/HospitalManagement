package com.kairos.dto.activity.counter.distribution.access_group;

import java.util.List;

public class AccessGroupPermissionCounterDTO {
    private Long staffId;
    private Long countryId;
    private boolean isCountryAdmin;
    private boolean management;
    private List<Long> accessGroupIds;

    public AccessGroupPermissionCounterDTO() {
    }

    public AccessGroupPermissionCounterDTO(Boolean isCountryAdmin, List<Long> accessGroupIds) {
        this.isCountryAdmin = isCountryAdmin;
        this.accessGroupIds = accessGroupIds;
    }

    public boolean isCountryAdmin() {
        return isCountryAdmin;
    }

    public void setCountryAdmin(boolean countryAdmin) {
        isCountryAdmin = countryAdmin;
    }

    public boolean isManagement() {
        return management;
    }

    public void setManagement(boolean management) {
        this.management = management;
    }

    public List<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(List<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }


}
