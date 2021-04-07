package com.kairos.dto.user.staff.staff_settings;

import com.kairos.enums.team.TeamType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TeamRankingInfoDTO {
    private BigInteger id;
    private Long teamId;
    private TeamType teamType;
    private Integer frequency;
}