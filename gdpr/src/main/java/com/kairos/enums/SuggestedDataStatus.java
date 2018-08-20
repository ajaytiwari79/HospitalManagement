package com.kairos.enums;

public enum  SuggestedDataStatus {

    IN_PROGRESS("inprogress"), ACCEPTED("accepted"), REJECTED("rejected"),QUEUE("inQueue");

    public String value;
    SuggestedDataStatus(String value) {
        this.value = value;
    }
}
