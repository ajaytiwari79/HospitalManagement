package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModelPermissionDTO {

    @NotNull(message = "message.permission.model.id.null")
    private Long permissionModelId;

    @Valid
    @NotEmpty(message = "message.permission.field.permissions.null")
    private List<FieldPermissionDTO> fieldPermissions= new ArrayList<>();

    @Valid
    private List<ModelPermissionDTO> subModelPermissions= new ArrayList<>();

    @NotNull(message = "message.model.permission.id.null")
    private FieldLevelPermission modelPermission;
}
