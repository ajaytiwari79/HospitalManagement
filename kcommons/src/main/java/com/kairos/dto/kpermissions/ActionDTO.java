package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
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
    private String actionName;
    private Set<OrganizationCategory> organizationCategories;
    private Set<FieldLevelPermission> permissions;

    public Set<FieldLevelPermission> getPermissions() {
        return isCollectionNotEmpty(permissions) ? permissions : new HashSet<>();
    }
}
