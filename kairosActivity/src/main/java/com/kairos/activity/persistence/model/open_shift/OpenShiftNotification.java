package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class OpenShiftNotification extends MongoBaseEntity {
    private BigInteger openShiftId;
    private Long staffId;
    private boolean response;

    public OpenShiftNotification() {
        //Default Constructor
    }

    public OpenShiftNotification(BigInteger openShiftId, Long staffId) {
        this.openShiftId = openShiftId;
        this.staffId = staffId;
    }

    public BigInteger getOpenShiftId() {
        return openShiftId;
    }

    public void setOpenShiftId(BigInteger openShiftId) {
        this.openShiftId = openShiftId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}
