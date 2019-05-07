package com.kairos.persistence.model.organization;
/*
 *Created By Pavan on 7/5/19
 *
 */

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
    private Long staffId;
    private Long teamId;
}
