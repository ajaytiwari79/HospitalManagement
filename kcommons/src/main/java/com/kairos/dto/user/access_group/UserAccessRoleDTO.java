package com.kairos.dto.user.access_group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserAccessRoleDTO {
    private Long userId;
    private Long unitId;
    private Boolean staff;
    private Boolean management;
    private Long staffId;
    private List<Long> accessGroupIds;

    public UserAccessRoleDTO(Boolean staff,Boolean management) {
        this.staff = staff;
        this.management = management;
    }

    public UserAccessRoleDTO(Long userId, Long unitId, Boolean staff, Boolean management){
        this.userId = userId;
        this.unitId = unitId;
        this.staff = staff;
        this.management = management;
    }

    public UserAccessRoleDTO(Long unitId, Boolean staff, Boolean management, Long staffId) {
        this.unitId = unitId;
        this.staff = staff;
        this.management = management;
        this.staffId = staffId;
    }

}
