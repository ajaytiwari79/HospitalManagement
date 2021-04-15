package com.kairos.dto.user.staff.staff;

import com.kairos.enums.team.TeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRankingInfoDTO {
    private Long teamId;
    private TeamType teamType;
    private BigInteger activityId;
    private Integer frequency;
}