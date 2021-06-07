package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNull;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class StaffTeamRanking extends UserBaseEntity {
    private Long unitId;
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Set<TeamRankingInfo> teamRankingInfo;
    private Long draftId;
    private LocalDateTime updatedOn;

    public StaffTeamRanking(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Set<TeamRankingInfo> teamRankingInfo, boolean published){
        this.unitId = unitId;
        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.teamRankingInfo = teamRankingInfo;
    }

    public LocalDateTime getUpdatedOn() {
        return isNull(super.getLastModificationDate()) ? super.getCreationDate() : super.getLastModificationDate();
    }
}
