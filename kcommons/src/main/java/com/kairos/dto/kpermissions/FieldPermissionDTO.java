package com.kairos.dto.kpermissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldPermissionDTO {

    @NotNull(message = "message.permission.action.id.null")
    Long fieldId;

    @NotBlank(message = "message.permission.id.null")
    String fieldPermission;
}