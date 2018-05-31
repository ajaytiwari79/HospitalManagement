package com.kairos.response.dto.web.open_shift;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundRules {
    private Integer candidatesPerRound;
    private DurationFields waitingTimeBeforeNextRound;
    private DurationFields checkAnswersTime;// Time to check answer after enquiry
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

    public DurationFields getWaitingTimeBeforeNextRound() {
        return waitingTimeBeforeNextRound;
    }

    public void setWaitingTimeBeforeNextRound(DurationFields waitingTimeBeforeNextRound) {
        this.waitingTimeBeforeNextRound = waitingTimeBeforeNextRound;
    }

    public DurationFields getCheckAnswersTime() {
        return checkAnswersTime;
    }

    public void setCheckAnswersTime(DurationFields checkAnswersTime) {
        this.checkAnswersTime = checkAnswersTime;
    }
}
