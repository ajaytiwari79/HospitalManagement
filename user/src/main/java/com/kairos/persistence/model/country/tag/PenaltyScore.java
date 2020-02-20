package com.kairos.persistence.model.country.tag;

import com.kairos.enums.PenaltyScoreLevel;
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
    private PenaltyScoreLevel penaltyScoreLevel;
    private int value;
}
