package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 6/5/19
 *
 */

import com.kairos.persistence.model.organization.StaffTeamRelationship;
import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class StaffTeamDTO {
    private Long staffId;
    private Long teamId;
    private String name;
    private StaffTeamRelationship.TeamType teamType;
    private StaffTeamRelationship.LeaderType leaderType;

}
