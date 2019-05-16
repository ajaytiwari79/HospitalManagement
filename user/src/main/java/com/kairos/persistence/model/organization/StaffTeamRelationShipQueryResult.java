package com.kairos.persistence.model.organization;
/*
 *Created By Pavan on 7/5/19
 *
 */

import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.staff.personal_details.Staff;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Getter
@Setter
public class StaffTeamRelationShipQueryResult {
    private Long id;
    private StaffTeamRelationship.LeaderType leaderType;
    private StaffTeamRelationship.TeamType teamType;
    private Staff staff;
    private Team team;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
