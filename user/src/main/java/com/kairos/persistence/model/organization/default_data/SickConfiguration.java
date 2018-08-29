package com.kairos.persistence.model.organization.default_data;

import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_UNIT;

/**
 * CreatedBy vipulpandey on 29/8/18
 **/
@NodeEntity
public class SickConfiguration {
    private Set<Long> timeTypes;
    @Relationship(type = IN_UNIT)
    private Organization organization;

    public Set<Long> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(Set<Long> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public SickConfiguration(Set<Long> timeTypes, Organization organization) {
        this.timeTypes = timeTypes;
        this.organization = organization;
    }
}
