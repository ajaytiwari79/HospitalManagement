package com.kairos.dto.user.organization;

import com.kairos.dto.user.staff.permission.StaffPermissionDTO;

import java.util.List;

/**
 * Created by oodles on 25/4/18.
 */
public class OrganizationResponseWrapper {

    OrganizationResponseDTO orgData;
    List<StaffPermissionDTO> permissions;

    public OrganizationResponseDTO getOrgData() {
        return orgData;
    }

    public void setOrgData(OrganizationResponseDTO orgData) {
        this.orgData = orgData;
    }

    public List<StaffPermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<StaffPermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
