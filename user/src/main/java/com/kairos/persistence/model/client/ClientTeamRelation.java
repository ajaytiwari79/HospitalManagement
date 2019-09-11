package com.kairos.persistence.model.client;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.SERVED_BY_TEAM;

/**
 * Created by oodles on 3/10/16.
 */

@RelationshipEntity(type = SERVED_BY_TEAM)
public class ClientTeamRelation extends UserBaseEntity {

    @StartNode
    private Client client;

    @EndNode
    private Team team;

    private ClientStaffRelation.StaffType type = ClientStaffRelation.StaffType.NONE;


    public ClientTeamRelation(Client client, Team team, ClientStaffRelation.StaffType staffType) {
        this.client = client;
        this.team = team;
        this.type = staffType;
    }

    public ClientTeamRelation() {
    }

    public ClientStaffRelation.StaffType getType() {
        return type;
    }

    public void setType(ClientStaffRelation.StaffType type) {
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
