package com.kairos.persistence.model.kpermissions;

import com.kairos.dto.kpermissions.ActionDTO;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class KPermissionModel extends UserBaseEntity {
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String modelName;

    private String modelClass;

    @Relationship(type = HAS_FIELD)
    private List<KPermissionField> fieldPermissions = new ArrayList<>();

    @Relationship(type = HAS_ACTION)
    private List<KPermissionAction> actionPermissions = new ArrayList<>();

    private boolean permissionSubModel;

    @Relationship(type = HAS_SUB_MODEL)
    private List<KPermissionModel> subModelPermissions = new ArrayList<>();

    private Set<OrganizationCategory> organizationCategories;


}
