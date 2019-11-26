package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ModelDTO {

    private Long id;
    private String modelName;
    private String modelClass;
    private boolean permissionSubModel;
    private List<FieldDTO> fieldPermissions = new ArrayList<>();
    private List<ModelDTO> subModelPermissions = new ArrayList<>();
    private Set<OrganizationCategory> organizationCategories;
    private Set<FieldLevelPermission> permissions;


}
