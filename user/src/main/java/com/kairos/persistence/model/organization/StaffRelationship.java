package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.personal_details.Staff;
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
    private Staff staff;


    boolean isEnabled = true;

    boolean teamLeader;

    public StaffRelationship() {
    }


    public StaffRelationship(Team team, Staff staff) {
        this.team = team;
        this.staff = staff;
    }


    public Team getTeam() {
        return team;
    }


    public void setTeam(Team team) {
        this.team = team;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(boolean teamLeader) {
        this.teamLeader = teamLeader;
    }
}


