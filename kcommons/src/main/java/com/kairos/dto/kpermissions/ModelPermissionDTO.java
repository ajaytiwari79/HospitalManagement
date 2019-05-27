package com.kairos.dto.kpermissions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

    @NotBlank(message = "message.model.permission.id.null")
    private String modelPermission;
}
