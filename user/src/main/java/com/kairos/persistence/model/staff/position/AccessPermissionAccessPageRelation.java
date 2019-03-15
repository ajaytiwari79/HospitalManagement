package com.kairos.persistence.model.staff.position;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_PAGE_PERMISSION;

/**
 * Created by prabjot on 7/12/16.
 */
@RelationshipEntity(type=HAS_ACCESS_PAGE_PERMISSION)
public class AccessPermissionAccessPageRelation extends UserBaseEntity {

    @StartNode
    private AccessPermission accessPermission;
    @EndNode
    private AccessPage accessPage;
    private boolean isRead;
    private boolean isWrite;
    private boolean isEnabled = true;


    public AccessPermissionAccessPageRelation(AccessPermission accessPermission, AccessPage accessPage) {
        this.accessPermission = accessPermission;
        this.accessPage = accessPage;
    }

    public AccessPermissionAccessPageRelation(AccessPermission accessPermission, AccessPage accessPage, boolean isRead, boolean isWrite) {
        this.accessPermission = accessPermission;
        this.accessPage = accessPage;
        this.isRead = isRead;
        this.isWrite = isWrite;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    public AccessPermissionAccessPageRelation() {
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setAccessPermission(AccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
