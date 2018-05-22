package com.kairos.response.dto.web.access_group;

public class UserAccessRoleDTO {
    private Long userId;
    private Long unitId;
    private Boolean staffGroup;
    private Boolean managementGroup;

    public UserAccessRoleDTO(){
        // default constructor
    }

    public UserAccessRoleDTO(Long userId, Long unitId, Boolean staffGroup, Boolean managementGroup){
        this.userId = userId;
        this.unitId = unitId;
        this.staffGroup = staffGroup;
        this.managementGroup = managementGroup;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Boolean getStaffGroup() {
        return staffGroup;
    }

    public void setStaffGroup(Boolean staffGroup) {
        this.staffGroup = staffGroup;
    }

    public Boolean getManagementGroup() {
        return managementGroup;
    }

    public void setManagementGroup(Boolean managementGroup) {
        this.managementGroup = managementGroup;
    }
}
