package com.kairos.persistence.model.staff.permission;

import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CUSTOMIZED_PERMISSION;

/**
 * Created by prerna on 15/3/18.
 */
@RelationshipEntity(type = HAS_CUSTOMIZED_PERMISSION)
public class UnitPermissionAccessPageRelation extends UserBaseEntity {

    @StartNode
    private UnitPermission unitPermission;
    @EndNode
    private AccessPage accessPage;
    @Property
    private boolean read;
    private boolean write;
    private Long accessGroupId;

    public UnitPermission getUnitPermission() {
        return unitPermission;
    }

    public void setUnitPermission(UnitPermission unitPermission) {
        this.unitPermission = unitPermission;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }
}
