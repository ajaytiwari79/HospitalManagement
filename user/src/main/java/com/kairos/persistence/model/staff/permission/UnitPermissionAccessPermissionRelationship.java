package com.kairos.persistence.model.staff.permission;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_PERMISSION;

/**
 * Created by prabjot on 30/1/17.
 */
@RelationshipEntity(type = HAS_ACCESS_PERMISSION)
@Getter
@Setter
public class UnitPermissionAccessPermissionRelationship extends UserBaseEntity {

    @StartNode private UnitPermission unitPermission;
    @EndNode   private AccessPermission accessPermission;
    @Property
    private boolean isEnabled  = true;

    public UnitPermissionAccessPermissionRelationship(UnitPermission unitPermission, AccessPermission accessPermission) {
        this.unitPermission = unitPermission;
        this.accessPermission = accessPermission;
    }
}
