package com.kairos.dto.user.staff.staff;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
@Getter
@Setter
public class UnitWiseStaffPermissionsDTO {

    private Boolean hub;
    private AccessGroupRole role;
    private HashMap<String, Object> hubPermissions;
    private HashMap<Long, Object> organizationPermissions;
}
