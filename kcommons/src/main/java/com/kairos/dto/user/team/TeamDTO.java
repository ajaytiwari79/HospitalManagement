package com.kairos.dto.user.team;


import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private TeamType teamType;
    private LeaderType leaderType;
}
