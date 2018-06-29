package com.kairos.user.staff.staff;

import com.kairos.user.access_permission.AccessGroupRole;

import java.util.List;

public class StaffAccessRoleDTO {
    private Long id;
    private List<AccessGroupRole>  roles;

    public StaffAccessRoleDTO() {
        //Default Constructor
    }

    public StaffAccessRoleDTO(Long id, List<AccessGroupRole> roles) {
        this.id = id;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AccessGroupRole> getRoles() {
        return roles;
    }

    public void setRoles(List<AccessGroupRole> roles) {
        this.roles = roles;
    }
}
