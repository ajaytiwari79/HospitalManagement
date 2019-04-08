package com.kairos.persistence.model.kpermissions;

import com.kairos.constants.FieldLevelPermissions;
import com.kairos.persistence.model.access_permission.AccessGroup;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@RelationshipEntity(type = HAS_PERMISSION)
public class AccessGroupPermissionFieldRelationshipType {


    @StartNode
    private PermissionField permissionField;

    @EndNode
    private AccessGroup accessGroup;

    @Property
    private FieldLevelPermissions fieldLevelPermissions;

    public AccessGroupPermissionFieldRelationshipType() {
    }

    public AccessGroupPermissionFieldRelationshipType(PermissionField permissionField, AccessGroup accessGroup, FieldLevelPermissions fieldLevelPermissions) {
        this.permissionField = permissionField;
        this.accessGroup = accessGroup;
        this.fieldLevelPermissions = fieldLevelPermissions;
    }

    public PermissionField getPermissionField() {
        return permissionField;
    }

    public void setPermissionField(PermissionField permissionField) {
        this.permissionField = permissionField;
    }

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public FieldLevelPermissions getFieldLevelPermissions() {
        return fieldLevelPermissions;
    }

    public void setFieldLevelPermissions(FieldLevelPermissions fieldLevelPermissions) {
        this.fieldLevelPermissions = fieldLevelPermissions;
    }
}
