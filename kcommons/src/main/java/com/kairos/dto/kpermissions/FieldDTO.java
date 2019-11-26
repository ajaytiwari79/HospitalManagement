package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FieldDTO {

    private Long id;
    private String fieldName;
    private Set<OrganizationCategory> organizationCategories;
    private Set<FieldLevelPermission> permissions;

}