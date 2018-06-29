package com.kairos.persistence.model.access_permission;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.constants.AppConstants.HAS_ACCESS_OF_TABS;

/**
 * Created by prabjot on 27/1/17.
 */
@RelationshipEntity(type=HAS_ACCESS_OF_TABS)
public class AccessGroupPageRelationShip extends UserBaseEntity {

    @StartNode private AccessGroup accessGroup;
    @EndNode private AccessPage accessPage;
    private boolean isEnabled;
    private boolean read;
    private boolean write;

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
}
