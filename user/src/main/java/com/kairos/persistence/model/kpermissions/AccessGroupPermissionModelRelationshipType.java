package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.*;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = HAS_PERMISSION)
public class AccessGroupPermissionModelRelationshipType extends UserBaseEntity {


    @StartNode
    private KPermissionModel kPermissionModel;

    @EndNode
    private AccessGroup accessGroup;

    @Property
    private FieldLevelPermission fieldLevelPermission;

}
