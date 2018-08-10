package com.kairos.persistence.model.open_shift;

import com.kairos.enums.open_shift.OpenShiftResponseStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class OpenShiftNotification extends MongoBaseEntity {
    private BigInteger openShiftId;
    private Long staffId;
    private OpenShiftResponseStatus responseStatus;

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
    public OpenShiftResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(OpenShiftResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }


}
