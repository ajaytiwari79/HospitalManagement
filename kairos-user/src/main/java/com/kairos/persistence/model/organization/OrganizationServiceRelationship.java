package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.PROVIDE_SERVICE;


/**
 * Created by prabjot on 11/1/17.
 */
@RelationshipEntity(type=PROVIDE_SERVICE)
public class OrganizationServiceRelationship extends UserBaseEntity {

    @StartNode
    private Organization organization;
    @EndNode
    private OrganizationService organizationService;
    private boolean isEnabled=true;

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Organization getOrganization() {
        return organization;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public OrganizationServiceRelationship(Organization organization, OrganizationService organizationService) {
        this.organization = organization;
        this.organizationService = organizationService;
    }

    public OrganizationServiceRelationship() {
    }
}
