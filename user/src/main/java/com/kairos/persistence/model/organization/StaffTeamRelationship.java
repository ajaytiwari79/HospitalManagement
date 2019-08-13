package com.kairos.persistence.model.organization;

import com.kairos.annotations.*;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_MEMBER;


/**
 * Created by oodles on 6/10/16.
 */
@KPermissionRelatedModel
@RelationshipEntity(type = TEAM_HAS_MEMBER)
public class StaffTeamRelationship extends UserBaseEntity {

    @KPermissionRelationshipTo
    @StartNode
    private Team team;

    @KPermissionRelationshipFrom
    @EndNode
    private Staff staff;

    private boolean isEnabled = true;
    private LeaderType leaderType;
    private TeamType teamType;



    public StaffTeamRelationship() {
        //Default Constructor
    }


    public StaffTeamRelationship(Team team, Staff staff) {
        this.team = team;
        this.staff = staff;
    }

    public StaffTeamRelationship(Team team, Staff staff, LeaderType leaderType) {
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
    }

    public StaffTeamRelationship(Long id,Team team, Staff staff, LeaderType leaderType, TeamType teamType) {
        this.id=id;
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
        this.teamType = teamType;
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

    public TeamType getTeamType() {
        return teamType;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    //Enum to set the leader type in team
    public enum LeaderType{
        MAIN_LEAD,ACTING_LEAD
    }

    //Enum to set the team type in team
    public  enum TeamType{
        MAIN,SECONDARY
    }
}


