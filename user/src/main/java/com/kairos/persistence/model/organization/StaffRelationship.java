package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.Staff;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_MEMBER;


/**
 * Created by oodles on 6/10/16.
 */

@RelationshipEntity(type = TEAM_HAS_MEMBER)
public class StaffRelationship extends UserBaseEntity {

    @StartNode
    private Team team;

    @EndNode
    private Staff user;


    boolean isEnabled = true;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Staff getUser() {
        return user;
    }

    public void setUser(Staff user) {
        this.user = user;
    }

    public StaffRelationship(Team team, Staff user) {

        this.team = team;
        this.user = user;
    }

    public StaffRelationship() {
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public enum StaffRole {

        PLANNER, VISITATOR, MANAGER, TEAM_LEADER;

        public String value;

        public static StaffRole getByValue(String value) {
            for (StaffRole role : StaffRole.values()) {
                if (role.value.equals(value)) {
                    return role;
                }
            }
            return null;
        }
    }
}


