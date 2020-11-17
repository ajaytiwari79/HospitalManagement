package com.kairos.dto.kpermissions;

import com.kairos.enums.kpermissions.PermissionAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionActionDTO {
    private String modelName;
    private PermissionAction action;


}
