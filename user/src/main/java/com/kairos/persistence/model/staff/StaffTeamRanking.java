package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;
import java.util.Set;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class StaffTeamRanking extends UserBaseEntity {
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Set<TeamRankingInfo> teamRankingInfo;
    private Long draftId;

    public StaffTeamRanking(Long staffId, LocalDate startDate, LocalDate endDate, Set<TeamRankingInfo> teamRankingInfo, boolean published){
        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.teamRankingInfo = teamRankingInfo;
    }
}
