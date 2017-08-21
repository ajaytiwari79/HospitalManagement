package com.kairos.persistence.model.user.staff;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_PAGE_PERMISSION;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.access_permission.AccessPage;

/**
 * Created by prabjot on 7/12/16.
 */
@RelationshipEntity(type=HAS_ACCESS_PAGE_PERMISSION)
public class EmploymentAccessPageRelation extends UserBaseEntity {

    @StartNode AccessPermission accessPermission;
    @EndNode
    AccessPage accessPage;
    private boolean isRead;
    private boolean isWrite;
    private boolean isEnabled = true;


    public EmploymentAccessPageRelation(AccessPermission accessPermission, AccessPage accessPage) {
        this.accessPermission = accessPermission;
        this.accessPage = accessPage;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    public EmploymentAccessPageRelation() {
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
