package com.kairos.enums;

public enum  AssessmentStatus {


    NEW("new"), ACCEPTED("accepted"), COMPLETED("completed");

    public String value;
    AssessmentStatus(String value) {
        this.value = value;
    }
}
