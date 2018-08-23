package com.kairos.enums;

public enum AssessmentType {


    Asset_TYPE("Asset"), PROCESSING_ACTIVITY_TYPE("Processing Activity");

    public String value;
    AssessmentType(String value) {
        this.value = value;
    }
}
