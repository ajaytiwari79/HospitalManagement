package com.kairos.enums;

public enum  SuggestedDataStatus {

    NEW("new"), ACCEPTED("accepted"), REJECTED("rejected");

    public String value;
    SuggestedDataStatus(String value) {
        this.value = value;
    }
}
