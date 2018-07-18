package com.planner.domain.vrpPlanning;

/**
 * @author pradeep
 * @date - 9/7/18
 */

public class ConstraintScore {
    private String constraintName;
    private Score score;

    public ConstraintScore(String constraintName, Score score) {
        this.constraintName = constraintName;
        this.score = score;
    }

    public ConstraintScore() {
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }
}
