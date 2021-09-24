package com.kairos.persistence.model.organization;
/*
 *Created By Pavan on 7/5/19
 *
 */

import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
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
    private LeaderType leaderType;
    private TeamType teamType;
    private Staff staff;
    private Team team;
    private int sequence;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
