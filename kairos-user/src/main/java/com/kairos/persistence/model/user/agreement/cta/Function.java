package com.kairos.persistence.model.user.agreement.cta;

public enum  Function {
    DEMENTIA_COORDINATOR("Dementia Coordinator"),TEAM_LEADER_IN_CROSSING_TEAM("Team leader In Crossing Team")
    ,FIRST_ASSISTANT("First Assistant"),STUDENT_COORDINATOR("Student Coordinator")
    ,TRAINING_COORDINATOR("Training Coordinator");
    private String function;
    Function(String function){
        this.function=function;
    }
}
