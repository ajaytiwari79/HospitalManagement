package com.kairos.enums;

public enum  AssessmentStatus {


    NEW("new"), INPROGRESS("inProgress"), COMPLETED("completed");

    public String value;
    AssessmentStatus(String value) {
        this.value = value;
    }
}
