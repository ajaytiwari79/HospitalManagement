package com.kairos.persistence.model.staff_settings;

import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRankingInfo extends MongoBaseEntity {
    private Long teamId;
    private TeamType teamType;
    private Integer frequency;
}
