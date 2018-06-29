package com.kairos.shiftplanning.domain.constraints;

public enum ScoreLevel {
    HARD("Hard"),MEDIUM("Medium"),SOFT("Soft");
    private final String level;
    ScoreLevel(String level){
        this.level=level;
    }
}
