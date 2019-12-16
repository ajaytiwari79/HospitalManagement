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
public class FieldDTO {

    private Long id;
    private String fieldName;
    private Set<OrganizationCategory> organizationCategories;
    private Set<FieldLevelPermission> permissions;
    private OtherPermissionDTO forOtherPermission;

    public Set<FieldLevelPermission> getPermissions() {
        return isCollectionNotEmpty(permissions) ? permissions : new HashSet<>();
    }
}