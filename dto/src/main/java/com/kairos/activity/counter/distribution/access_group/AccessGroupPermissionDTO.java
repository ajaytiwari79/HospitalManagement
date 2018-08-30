package com.kairos.activity.counter.distribution.access_group;

import java.util.List;

public class AccessGroupPermissionDTO {
    private Boolean isCountryAdmin;
    private List<Long> accessGroupIds;

    public AccessGroupPermissionDTO() {
    }

    public AccessGroupPermissionDTO(Boolean isCountryAdmin, List<Long> accessGroupIds) {
        this.isCountryAdmin = isCountryAdmin;
        this.accessGroupIds = accessGroupIds;
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
