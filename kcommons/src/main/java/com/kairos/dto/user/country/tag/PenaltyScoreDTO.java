package com.kairos.dto.user.country.tag;

import com.kairos.enums.PenaltyScoreLevel;
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
public class PenaltyScoreDTO {
    private Long id;
    private PenaltyScoreLevel penaltyScoreLevel;
    private int value;
}
