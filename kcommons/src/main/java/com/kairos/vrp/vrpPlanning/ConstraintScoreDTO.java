package com.kairos.vrp.vrpPlanning;


/**
 * @author pradeep
 * @date - 9/7/18
 */

public class ConstraintScoreDTO {
    private String constraintName;
    private ScoreDTO score;

    public ConstraintScoreDTO(String constraintName, ScoreDTO score) {
        this.constraintName = constraintName;
        this.score = score;
    }

    public ConstraintScoreDTO() {
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public ScoreDTO getScore() {
        return score;
    }

    public void setScore(ScoreDTO score) {
        this.score = score;
    }
}
