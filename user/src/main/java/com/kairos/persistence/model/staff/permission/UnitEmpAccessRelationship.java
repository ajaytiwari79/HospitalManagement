package com.kairos.persistence.model.staff.permission;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_PERMISSION;

/**
 * Created by prabjot on 30/1/17.
 */
//TODO remove
@RelationshipEntity(type = HAS_ACCESS_PERMISSION)
public class UnitEmpAccessRelationship extends UserBaseEntity {

    @StartNode private UnitPermission unitPermission;
    @EndNode private AccessPermission accessPermission;
    @Property
    private boolean isEnabled  = true;

    public UnitEmpAccessRelationship(UnitPermission unitPermission, AccessPermission accessPermission) {
        this.unitPermission = unitPermission;
        this.accessPermission = accessPermission;
    }

    public UnitPermission getUnitPermission() {
        return unitPermission;
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public void setUnitPermission(UnitPermission unitPermission) {
        this.unitPermission = unitPermission;
    }

    public void setAccessPermission(AccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
