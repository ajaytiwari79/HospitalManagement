package com.kairos.enums.payout;

/**
 * @author pradeep
 * @date - 18/7/18
 */

public enum PayOutTrasactionStatus {

    REQUESTED("Requested"),APPROVED("Approved"),PAIDOUT("Paid Out");

    String value;

    PayOutTrasactionStatus(String value) {
        this.value = value;
    }
}
