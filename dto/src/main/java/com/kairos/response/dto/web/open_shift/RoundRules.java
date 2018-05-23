package com.kairos.response.dto.web.open_shift;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundRules {
    private Integer candidatesPerRound;
    private Integer waitingTimeBeforeNextRoundInDays;
    private Integer checkAnswersInDays;// Time to check answer after enquiry
    private Integer minimumCandidateBeforeSelection;

    public RoundRules() {
        //Default Constructor
    }

    public RoundRules(Integer maximumConcurrentEnquiries, Integer candidatesPerRound, Integer waitingTimeBeforeNextRoundInDays, Integer minimumCandidateBeforeSelection) {
        this.candidatesPerRound = candidatesPerRound;
        this.waitingTimeBeforeNextRoundInDays = waitingTimeBeforeNextRoundInDays;
        this.minimumCandidateBeforeSelection = minimumCandidateBeforeSelection;
    }

    public Integer getCandidatesPerRound() {
        return candidatesPerRound;
    }

    public void setCandidatesPerRound(Integer candidatesPerRound) {
        this.candidatesPerRound = candidatesPerRound;
    }

    public Integer getWaitingTimeBeforeNextRoundInDays() {
        return waitingTimeBeforeNextRoundInDays;
    }

    public void setWaitingTimeBeforeNextRoundInDays(Integer waitingTimeBeforeNextRoundInDays) {
        this.waitingTimeBeforeNextRoundInDays = waitingTimeBeforeNextRoundInDays;
    }

    public Integer getMinimumCandidateBeforeSelection() {
        return minimumCandidateBeforeSelection;
    }

    public void setMinimumCandidateBeforeSelection(Integer minimumCandidateBeforeSelection) {
        this.minimumCandidateBeforeSelection = minimumCandidateBeforeSelection;
    }

    public Integer getCheckAnswersInDays() {
        return checkAnswersInDays;
    }

    public void setCheckAnswersInDays(Integer checkAnswersInDays) {
        this.checkAnswersInDays = checkAnswersInDays;
    }
}
