package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.services.OrganizationService;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.LINK_WITH_EXTERNAL_SERVICE;

/**
 * Created by Jasgeet on 28/9/17.
 */
@RelationshipEntity(type = LINK_WITH_EXTERNAL_SERVICE)
public class OrganizationExternalServiceRelationship extends UserBaseEntity {
    @StartNode
    OrganizationService service;

    @EndNode
    OrganizationService externalService;

    public OrganizationService getService() {
        return service;
    }

    public void setService(OrganizationService service) {
        this.service = service;
    }

    public OrganizationService getExternalService() {
        return externalService;
    }

    public void setExternalService(OrganizationService externalService) {
        this.externalService = externalService;
    }
}
