package com.kairos.persistence.model.user.access_profile;


import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.auth.User;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_ACCESS_GROUP;

/**
 * Created by prabjot on 9/27/16.
 */
@RelationshipEntity(type = STAFF_HAS_ACCESS_GROUP)
public class UserAccessProfileRelationship extends UserBaseEntity {

    @GraphId
    Long id;

    @StartNode
    private User user;
    @EndNode
    private AccessGroup accessGroup;

    private Long organizationId;
    


    public UserAccessProfileRelationship(User user, AccessGroup accessGroup, Long organizationId) {
        this.user = user;
        this.accessGroup = accessGroup;
        this.organizationId = organizationId;
    }

    public UserAccessProfileRelationship(){}
}
