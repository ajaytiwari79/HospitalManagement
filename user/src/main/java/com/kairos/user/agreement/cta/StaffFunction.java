package com.kairos.user.agreement.cta;

public enum StaffFunction {
    DEMENTIA_COORDINATOR("Dementia Coordinator"),TEAM_LEADER_IN_CROSSING_TEAM("Team leader In Crossing Team")
    ,FIRST_ASSISTANT("First Assistant"),STUDENT_COORDINATOR("Student Coordinator")
    ,TRAINING_COORDINATOR("Training Coordinator"),SELECT_FROM_UNIT_LIST("Select From Unit List");
    private String function;
    StaffFunction(String function){
        this.function=function;
    }
}
