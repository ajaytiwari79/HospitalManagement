package com.kairos.dto.kpermissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    @NotEmpty(message = "message.accessGroup.id.null")
    private List<Long> accessGroupIds;

    @Valid
    @NotEmpty(message = "message.model.permissions.null")
    private List<ModelPermissionDTO> modelPermissions;
}
