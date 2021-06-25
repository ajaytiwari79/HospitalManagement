package com.kairos.enums.gdpr;

import java.io.Serializable;

public enum  SuggestedDataStatus implements Serializable {

    PENDING("Pending"), APPROVED("Approved"), REJECTED("Rejected");

    public String value;
    SuggestedDataStatus(String value) {
        this.value = value;
    }
}
