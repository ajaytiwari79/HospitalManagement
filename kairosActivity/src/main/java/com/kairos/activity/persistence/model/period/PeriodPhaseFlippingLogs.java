package com.kairos.activity.persistence.model.period;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by prerna on 12/4/18.
 */
public class PeriodPhaseFlippingLogs {

    private BigInteger fromPhaseId;
    private BigInteger toPhaseId;
    private Date flippingDate;
    private Long flippindDoneByUserId;

    public PeriodPhaseFlippingLogs(){
        // default constructor
    }

    public BigInteger getFromPhaseId() {
        return fromPhaseId;
    }

    public void setFromPhaseId(BigInteger fromPhaseId) {
        this.fromPhaseId = fromPhaseId;
    }

    public BigInteger getToPhaseId() {
        return toPhaseId;
    }

    public void setToPhaseId(BigInteger toPhaseId) {
        this.toPhaseId = toPhaseId;
    }

    public Date getFlippingDate() {
        return flippingDate;
    }

    public void setFlippingDate(Date flippingDate) {
        this.flippingDate = flippingDate;
    }

    public Long getFlippindDoneByUserId() {
        return flippindDoneByUserId;
    }

    public void setFlippindDoneByUserId(Long flippindDoneByUserId) {
        this.flippindDoneByUserId = flippindDoneByUserId;
    }
}
