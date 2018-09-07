package com.kairos.enums;

public enum  SuggestedDataStatus {

    PENDING("Pending"), APPROVED("Approved"), REJECTED("Rejected");

    public String value;
    SuggestedDataStatus(String value) {
        this.value = value;
    }
}
