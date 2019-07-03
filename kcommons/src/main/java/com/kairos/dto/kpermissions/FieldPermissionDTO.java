package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldPermissionDTO {

    @NotNull(message = "message.permission.field.id.null")
    private Long fieldId;

    @NotNull(message = "message.permission.id.null")
    private FieldLevelPermission fieldPermission;
}