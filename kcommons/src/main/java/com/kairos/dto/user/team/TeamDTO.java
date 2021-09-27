package com.kairos.dto.user.team;


import com.kairos.enums.team.LeaderType;
import com.kairos.enums.team.TeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private TeamType teamType;
    private LeaderType leaderType;
    private BigInteger activityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int sequence;
    private boolean teamMembership;

    public TeamDTO(Long id, TeamType teamType, LocalDate startDate, LocalDate endDate){
        this.id = id;
        this.teamType = teamType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
