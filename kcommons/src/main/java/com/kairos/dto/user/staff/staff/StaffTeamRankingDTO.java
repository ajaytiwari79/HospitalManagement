package com.kairos.dto.user.staff.staff;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class StaffTeamRankingDTO {
    private Long id;
    private Long unitId;
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Set<TeamRankingInfoDTO> teamRankingInfo;
    private LocalDateTime updatedOn;
}
