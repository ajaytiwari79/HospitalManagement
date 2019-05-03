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
public class StaffTeamRelationship extends UserBaseEntity {

    @StartNode
    private Team team;

    @EndNode
    private Staff staff;

    private boolean isEnabled = true;
    private LeaderType leaderType;



    public StaffTeamRelationship() {
        //Default Constructor
    }


    public StaffTeamRelationship(Team team, Staff staff) {
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public LeaderType getLeaderType() {
        return leaderType;
    }

    public void setLeaderType(LeaderType leaderType) {
        this.leaderType = leaderType;
    }

    //Enum to set the leader type in team
    public enum LeaderType{
        MAIN_LEAD,ACTING_LEAD
    }
}


