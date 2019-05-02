package com.kairos.persistence.model.organization.services;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CUSTOM_SERVICE_NAME_FOR;

/**
 * Created by prerna on 15/11/17.
 */
@RelationshipEntity(type=HAS_CUSTOM_SERVICE_NAME_FOR)
public class OrganizationServiceCustomNameRelationship extends UserBaseEntity {
    @StartNode
    private Organization organization;
    @EndNode
    private OrganizationService organizationService;
    private String customName;

    public OrganizationServiceCustomNameRelationship(){
            //Default Constructor
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
