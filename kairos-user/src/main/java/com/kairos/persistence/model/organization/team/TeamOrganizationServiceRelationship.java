package com.kairos.persistence.model.organization.team;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationService;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_SERVICES;


/**
 * Created by prabjot on 20/2/17.
 */
@RelationshipEntity(type=TEAM_HAS_SERVICES)
public class TeamOrganizationServiceRelationship extends UserBaseEntity {

    @StartNode
    private Team team;
    @EndNode
    private OrganizationService organizationService;
    private boolean isEnabled = true;

    public Team getTeam() {
        return team;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
