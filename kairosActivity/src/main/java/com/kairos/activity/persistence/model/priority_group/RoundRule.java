package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundRule {
    private Integer maximumConcurrentEnquiries;
    private Integer candidatesPerRound;
    private Integer waitingTimeBeforeNextRoundInDays;
    private Integer minimumCandidateBeforeSelection;

    public RoundRule() {
        //Default Constructor
    }

    public RoundRule(Integer maximumConcurrentEnquiries, Integer candidatesPerRound, Integer waitingTimeBeforeNextRoundInDays, Integer minimumCandidateBeforeSelection) {
        this.maximumConcurrentEnquiries = maximumConcurrentEnquiries;
        this.candidatesPerRound = candidatesPerRound;
        this.waitingTimeBeforeNextRoundInDays = waitingTimeBeforeNextRoundInDays;
        this.minimumCandidateBeforeSelection = minimumCandidateBeforeSelection;
    }

    public Integer getMaximumConcurrentEnquiries() {
        return maximumConcurrentEnquiries;
    }

    public void setMaximumConcurrentEnquiries(Integer maximumConcurrentEnquiries) {
        this.maximumConcurrentEnquiries = maximumConcurrentEnquiries;
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
}
