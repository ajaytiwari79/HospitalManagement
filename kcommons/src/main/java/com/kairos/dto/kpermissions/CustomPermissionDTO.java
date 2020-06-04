package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CustomPermissionDTO {
    private Long staffId;
    private Long id;
    private Set<FieldLevelPermission> permissions;
}
