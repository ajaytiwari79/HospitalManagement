package com.kairos.response.dto.web.access_group;

public class UserAccessRoleDTO {
    private Long userId;
    private Long unitId;
    private Boolean staff;
    private Boolean management;

    public UserAccessRoleDTO(){
        // default constructor
    }

    public UserAccessRoleDTO(Long userId, Long unitId, Boolean staff, Boolean management){
        this.userId = userId;
        this.unitId = unitId;
        this.staff = staff;
        this.management = management;
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

    public Boolean getStaff() {
        return staff;
    }

    public void setStaff(Boolean staff) {
        this.staff = staff;
    }

    public Boolean getManagement() {
        return management;
    }

    public void setManagement(Boolean management) {
        this.management = management;
    }
}
