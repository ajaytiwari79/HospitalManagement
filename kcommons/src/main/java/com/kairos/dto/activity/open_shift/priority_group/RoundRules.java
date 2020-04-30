
package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.open_shift.DurationField;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class RoundRules {
    private Integer candidatesPerRound;
    private DurationField waitingTimeBeforeNextRound;
    private DurationField checkAnswersTime;// Time to check answer after enquiry
    private Integer minimumCandidateBeforeSelection;
}