package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 6/5/19
 *
 */

import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class StaffTeamDTO {
    private Long staffId;
    private Long teamId;
    private String name;
    private TeamType teamType;
    private LeaderType leaderType;

}
