package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.PermissionAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ActionDTO implements Serializable {

    private static final long serialVersionUID = -4122574596809283496L;
    private Long id;
    private String modelName;
    private Set<OrganizationCategory> organizationCategories;
    private PermissionAction action;

    public ActionDTO(String modelName, PermissionAction action) {
        this.modelName = modelName;
        this.action = action;
    }
}
