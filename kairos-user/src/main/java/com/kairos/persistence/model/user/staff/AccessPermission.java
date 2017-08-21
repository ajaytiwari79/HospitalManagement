package com.kairos.persistence.model.user.staff;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_GROUP;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.access_permission.AccessGroup;

/**
 * Created by prabjot on 9/12/16.
 */
@NodeEntity
public class AccessPermission extends UserBaseEntity{

    @Relationship(type = HAS_ACCESS_GROUP)
    AccessGroup accessGroup;

    public AccessPermission() {
    }

    public AccessPermission(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }
}
