package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = HAS_PERMISSION)
public class AccessGroupPermissionFieldRelationshipType extends UserBaseEntity {


    @StartNode
    private KPermissionField kPermissionField;

    @EndNode
    private AccessGroup accessGroup;

    @Property
    private FieldLevelPermission fieldLevelPermissions;


    private Set<Long> expertiseIds;
    private Set<Long> unionIds;
    private Set<Long> teamIds;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<StaffStatusEnum> staffStatuses;
    private FieldLevelPermission forOtherFieldLevelPermissions;

}
