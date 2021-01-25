package com.kairos.dto.kpermissions;

import com.kairos.dto.TranslationInfo;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Getter
@Setter
@NoArgsConstructor
public class ModelDTO implements Serializable {

    private static final long serialVersionUID = 4335555182715765843L;
    private Long id;
    private String modelName;
    private String modelClass;
    private boolean permissionSubModel;
    private List<FieldDTO> fieldPermissions = new ArrayList<>();
    private List<ActionDTO> actionPermissions = new ArrayList<>();
    private List<ModelDTO> subModelPermissions = new ArrayList<>();
    private Set<OrganizationCategory> organizationCategories;
    private Set<FieldLevelPermission> permissions;
    private OtherPermissionDTO forOtherPermissions;
    private Map<String, TranslationInfo> translations;

    public Set<FieldLevelPermission> getPermissions() {
        return isCollectionNotEmpty(permissions) ? permissions : new HashSet<>();
    }

    public OtherPermissionDTO getForOtherPermissions() {
        return isNotNull(forOtherPermissions) ? forOtherPermissions : new OtherPermissionDTO();
    }
}
