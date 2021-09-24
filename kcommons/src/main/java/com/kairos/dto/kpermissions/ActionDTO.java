package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.kpermissions.PermissionAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ActionDTO {

    private Long id;
    private String modelName;
    private Set<OrganizationCategory> organizationCategories;
    private PermissionAction action;

    public ActionDTO(String modelName, PermissionAction action) {
        this.modelName = modelName;
        this.action = action;
    }
}
