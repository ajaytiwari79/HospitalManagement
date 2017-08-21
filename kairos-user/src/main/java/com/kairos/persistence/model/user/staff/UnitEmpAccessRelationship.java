package com.kairos.persistence.model.user.staff;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 30/1/17.
 */
@RelationshipEntity(type = HAS_ACCESS_PERMISSION)
public class UnitEmpAccessRelationship extends UserBaseEntity {

    @StartNode private UnitEmployment unitEmployment;
    @EndNode private AccessPermission accessPermission;
    @Property
    private boolean isEnabled  = true;

    public UnitEmployment getUnitEmployment() {
        return unitEmployment;
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public void setUnitEmployment(UnitEmployment unitEmployment) {
        this.unitEmployment = unitEmployment;
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
