package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 6/5/19
 *
 */

import com.kairos.persistence.model.organization.StaffTeamRelationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;

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
