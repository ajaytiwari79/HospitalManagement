package com.kairos.persistence.model.organization;

import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.annotations.KPermissionRelationshipFrom;
import com.kairos.annotations.KPermissionRelationshipTo;
import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.personal_details.Staff;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigInteger;
import java.time.LocalDate;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_MEMBER;


/**
 * Created by oodles on 6/10/16.
 */
@Getter
@Setter
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
    private LocalDate startDate;
    private LocalDate endDate;
    private int sequence;

    public StaffTeamRelationship(Team team, Staff staff, LeaderType leaderType, TeamType teamType, LocalDate startDate, LocalDate endDate) {
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
        this.teamType = teamType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public StaffTeamRelationship(Team team, Staff staff, LeaderType leaderType,LocalDate startDate,LocalDate endDate) {
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
        this.startDate =startDate;
        this.endDate =endDate;
    }

    public StaffTeamRelationship(Long id,Team team, Staff staff, LeaderType leaderType, TeamType teamType) {
        this.id=id;
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
        this.teamType = teamType;
    }

    public StaffTeamRelationship(Long id,Team team, Staff staff, LeaderType leaderType, TeamType teamType,LocalDate startDate, LocalDate endDate,int sequence) {
        this.id=id;
        this.team = team;
        this.staff = staff;
        this.leaderType = leaderType;
        this.teamType = teamType;
        this.startDate =startDate;
        this.endDate =endDate;
        this.sequence =sequence;
    }

}


