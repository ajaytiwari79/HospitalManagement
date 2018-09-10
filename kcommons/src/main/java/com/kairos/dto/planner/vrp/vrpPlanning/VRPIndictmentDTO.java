package com.kairos.dto.planner.vrp.vrpPlanning;


import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 9/7/18
 */

public class VRPIndictmentDTO {

    private BigInteger solverConfigId;
    private ScoreDTO totalScore;
    private List<ConstraintScoreDTO> scores;

    public VRPIndictmentDTO(BigInteger solverConfigId, ScoreDTO totalScore, List<ConstraintScoreDTO> scores) {
        this.solverConfigId = solverConfigId;
        this.totalScore = totalScore;
        this.scores = scores;
    }

    public VRPIndictmentDTO() {
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

    public ScoreDTO getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(ScoreDTO totalScore) {
        this.totalScore = totalScore;
    }

    public List<ConstraintScoreDTO> getScores() {
        return scores;
    }

    public void setScores(List<ConstraintScoreDTO> scores) {
        this.scores = scores;
    }
}
