package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 6/5/19
 *
 */

import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

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
    private LocalDate startDate;
    private LocalDate endDate;
    private int sequence;
    private boolean teamMember;

}
