package com.kairos.enums;

public enum  SuggestedDataStatus {

    APPROVAL_PENDING("Approval pending"), ACCEPTED("Accepted"), REJECTED("Rejected");

    public String value;
    SuggestedDataStatus(String value) {
        this.value = value;
    }
}
