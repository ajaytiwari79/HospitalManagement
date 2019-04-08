package com.kairos.persistence.model.staff.permission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_GROUP;


/**
 * Created by prabjot on 9/12/16.
 */
@NodeEntity
public class AccessPermission extends UserBaseEntity {

    @Relationship(type = HAS_ACCESS_GROUP)
    AccessGroup accessGroup;

    public AccessPermission() {
    }

    public AccessPermission(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }
}
