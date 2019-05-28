package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERSONALIZED_LOCATION;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
@RelationshipEntity(type = HAS_PERSONALIZED_LOCATION)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationPersonalizeLocationRelationShip extends UserBaseEntity {
    @StartNode
    private Unit unit;
    @EndNode
    private Expertise expertise;
    private Long locationId;
}
