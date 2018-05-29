package com.kairos.response.dto.web.open_shift;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundRules {
    private Integer candidatesPerRound;
    private Integer waitingTimeBeforeNextRound;
    private Integer checkAnswersTime;// Time to check answer after enquiry
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

    public Integer getWaitingTimeBeforeNextRound() {
        return waitingTimeBeforeNextRound;
    }

    public void setWaitingTimeBeforeNextRound(Integer waitingTimeBeforeNextRound) {
        this.waitingTimeBeforeNextRound = waitingTimeBeforeNextRound;
    }

    public Integer getCheckAnswersTime() {
        return checkAnswersTime;
    }

    public void setCheckAnswersTime(Integer checkAnswersTime) {
        this.checkAnswersTime = checkAnswersTime;
    }
}
