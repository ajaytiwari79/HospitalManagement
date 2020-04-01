package com.kairos.shiftplanning.domain.staff;

import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    private Long id;
    private TeamType teamType;
    private String name;
    private LeaderType leaderType;

}
