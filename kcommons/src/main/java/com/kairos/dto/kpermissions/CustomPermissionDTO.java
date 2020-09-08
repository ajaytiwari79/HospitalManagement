package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.kpermissions.PermissionAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CustomPermissionDTO {
    private Long staffId;
    private Long id;
    private Set<FieldLevelPermission> permissions;
    private OtherPermissionDTO forOtherPermissions;
    private boolean forOtherStaff;
    private Set<Long> actions;
    private Long actionId;
    private boolean hasAccess;
    private Long accessGroupId;
}
