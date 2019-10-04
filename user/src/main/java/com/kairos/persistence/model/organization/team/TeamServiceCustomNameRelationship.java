package com.kairos.persistence.model.organization.team;

import com.kairos.persistence.model.organization.services.OrganizationService;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CUSTOM_SERVICE_NAME_FOR;

/**
 * Created by prerna on 3/1/18.
 */
@RelationshipEntity(type=HAS_CUSTOM_SERVICE_NAME_FOR)
public class TeamServiceCustomNameRelationship {

    @StartNode
    private Team team;
    @EndNode
    private OrganizationService organizationService;
    private String customName;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

