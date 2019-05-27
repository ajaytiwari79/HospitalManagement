package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERSONALIZED_LOCATION;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
@RelationshipEntity(type = HAS_PERSONALIZED_LOCATION)
public class OrganizationPersonalizeLocationRelationShip extends UserBaseEntity {
    @StartNode
    private Unit unit;
    @EndNode
    private Expertise expertise;
    private Long locationId;

    public OrganizationPersonalizeLocationRelationShip() {

    }

    public OrganizationPersonalizeLocationRelationShip(Unit unit, Expertise expertise, Long locationId) {
        this.unit = unit;
        this.expertise = expertise;
        this.locationId = locationId;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
