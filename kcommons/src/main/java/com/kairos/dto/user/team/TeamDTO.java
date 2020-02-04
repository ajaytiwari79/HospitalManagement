package com.kairos.dto.user.team;


import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private TeamType teamType;
    private LeaderType leaderType;
    private Set<BigInteger> activityIds;
}
