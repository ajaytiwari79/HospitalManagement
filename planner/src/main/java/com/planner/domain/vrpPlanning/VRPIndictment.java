package com.planner.domain.vrpPlanning;

import com.planner.domain.MongoBaseEntity;
import org.optaplanner.core.api.score.constraint.Indictment;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 9/7/18
 */

public class VRPIndictment extends MongoBaseEntity {

    private BigInteger solverConfigId;
    private Score totalScore;
    private List<ConstraintScore> scores;

    public VRPIndictment(BigInteger solverConfigId, Score totalScore, List<ConstraintScore> scores) {
        this.solverConfigId = solverConfigId;
        this.totalScore = totalScore;
        this.scores = scores;
    }

    public VRPIndictment() {
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

    public Score getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Score totalScore) {
        this.totalScore = totalScore;
    }

    public List<ConstraintScore> getScores() {
        return scores;
    }

    public void setScores(List<ConstraintScore> scores) {
        this.scores = scores;
    }
}
