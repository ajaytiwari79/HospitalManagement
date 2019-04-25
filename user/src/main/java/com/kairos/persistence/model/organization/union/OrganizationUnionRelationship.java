package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_HAS_UNIONS;

/**
 * Created by vipul on 14/2/18.
 */
@RelationshipEntity(type = ORGANIZATION_HAS_UNIONS)
public class OrganizationUnionRelationship {
    @StartNode
    private Organization organization;
    @EndNode
    private Organization union;

    private boolean disabled;
    private Long dateOfJoining;
    private Long dateOfSeparation;

    public OrganizationUnionRelationship() {
        //Default Constructor
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Long getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(Long dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public Long getDateOfSeparation() {
        return dateOfSeparation;
    }

    public void setDateOfSeparation(Long dateOfSeparation) {
        this.dateOfSeparation = dateOfSeparation;
    }
}
