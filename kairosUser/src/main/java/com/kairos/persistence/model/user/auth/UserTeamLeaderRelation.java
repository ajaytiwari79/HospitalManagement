package com.kairos.persistence.model.user.auth;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TEAM_LEADER;


/**
 * Created by oodles on 21/10/16.
 */
@RelationshipEntity(type = HAS_TEAM_LEADER)
public class UserTeamLeaderRelation extends UserBaseEntity {

    @GraphId
    private Long id;

    @StartNode
    private Team team;
    @EndNode
    private User user;

    String[] permissions;


    public UserTeamLeaderRelation() {
    }

    @Override
    public Long getId() {
        return id;
    }



    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public UserTeamLeaderRelation(Team team, User user) {
        this.team = team;
        this.user = user;
    }
}
