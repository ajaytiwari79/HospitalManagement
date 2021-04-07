package com.kairos.persistence.model.staff_settings;

import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRankingInfo extends MongoBaseEntity {
    private Long teamId;
    private TeamType teamType;
    private Integer rank;
    private Integer frequency;
}
