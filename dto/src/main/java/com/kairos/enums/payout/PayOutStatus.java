package com.kairos.enums.payout;

/**
 * @author pradeep
 * @date - 18/7/18
 */

public enum PayOutStatus {

    APPROVED("Approved"),COMPLETED("Completed");
    String value;

    PayOutStatus(String value) {
        this.value = value;
    }
}
