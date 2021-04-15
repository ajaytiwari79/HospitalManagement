package com.kairos.persistence.model.staff;

import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigInteger;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRankingInfo extends UserBaseEntity {
    private Long teamId;
    private TeamType teamType;
    private BigInteger activityId;
    private Integer frequency;
}
