package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldPermissionDTO {

    @NotNull(message = "message.permission.field.id.null")
    private Long fieldId;

    @NotNull(message = "message.permission.id.null")
    private Set<FieldLevelPermission> fieldPermissions;
}