package com.kairos.dto.user.staff.staff_settings;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class StaffTeamRankingDTO {
    private BigInteger id;
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Set<TeamRankingInfoDTO> teamRankingInfo;
    private Date updatedAt;
}
