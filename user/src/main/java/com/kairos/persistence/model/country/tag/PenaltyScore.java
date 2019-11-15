package com.kairos.persistence.model.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.PenaltyScoreLevel;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created By G.P.Ranjan on 7/11/19
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PenaltyScore extends UserBaseEntity {
    private PenaltyScoreLevel penaltyScoreLevel;
    private int value;
}
