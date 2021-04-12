package com.kairos.persistence.model.staff_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class StaffTeamRanking extends MongoBaseEntity {
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Set<TeamRankingInfo> teamRankingInfo;
    private BigInteger draftId;

    public StaffTeamRanking(Long staffId, LocalDate startDate, LocalDate endDate, Set<TeamRankingInfo> teamRankingInfo){
        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = true;
        this.teamRankingInfo = teamRankingInfo;
    }
}
