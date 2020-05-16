package com.kairos.persistence.model.country.tag;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By G.P.Ranjan on 7/11/19
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyScore extends UserBaseEntity {
    private ScoreLevel penaltyScoreLevel;
    private int value;
}
