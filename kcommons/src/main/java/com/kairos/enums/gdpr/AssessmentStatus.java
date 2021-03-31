package com.kairos.enums.gdpr;

import java.io.Serializable;

public enum  AssessmentStatus implements Serializable {


    NEW("new"), IN_PROGRESS("inProgress"), COMPLETED("completed");

    public String value;
    AssessmentStatus(String value) {
        this.value = value;
    }
}
