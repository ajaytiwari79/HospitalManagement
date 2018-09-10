
package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.open_shift.DurationField;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundRules {
    private Integer candidatesPerRound;
    private DurationField waitingTimeBeforeNextRound;
    private DurationField checkAnswersTime;// Time to check answer after enquiry
    private Integer minimumCandidateBeforeSelection;

    public RoundRules() {
        //Default Constructor
    }

    public Integer getCandidatesPerRound() {
        return candidatesPerRound;
    }

    public void setCandidatesPerRound(Integer candidatesPerRound) {
        this.candidatesPerRound = candidatesPerRound;
    }

    public Integer getMinimumCandidateBeforeSelection() {
        return minimumCandidateBeforeSelection;
    }

    public void setMinimumCandidateBeforeSelection(Integer minimumCandidateBeforeSelection) {
        this.minimumCandidateBeforeSelection = minimumCandidateBeforeSelection;
    }

    public DurationField getWaitingTimeBeforeNextRound() {
        return waitingTimeBeforeNextRound;
    }

    public void setWaitingTimeBeforeNextRound(DurationField waitingTimeBeforeNextRound) {
        this.waitingTimeBeforeNextRound = waitingTimeBeforeNextRound;
    }

    public DurationField getCheckAnswersTime() {
        return checkAnswersTime;
    }

    public void setCheckAnswersTime(DurationField checkAnswersTime) {
        this.checkAnswersTime = checkAnswersTime;
    }
}