package com.kairos.service.pay_out;

import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.time.LocalDate;

/**
 * @author pradeep
 * @date - 18/7/18
 */

public class PayOutTransaction extends MongoBaseEntity{

    private Long staffId;
    private Long employmentId;
    private PayOutTrasactionStatus payOutTrasactionStatus;
    private boolean processed;
    private int minutes;
    private LocalDate date;


    public PayOutTransaction() {
    }

    public PayOutTransaction(Long staffId, Long employmentId, PayOutTrasactionStatus payOutTrasactionStatus, int minutes, LocalDate date) {
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.payOutTrasactionStatus = payOutTrasactionStatus;
        this.minutes = minutes;
        this.date = date;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public PayOutTrasactionStatus getPayOutTrasactionStatus() {
        return payOutTrasactionStatus;
    }

    public void setPayOutTrasactionStatus(PayOutTrasactionStatus payOutTrasactionStatus) {
        this.payOutTrasactionStatus = payOutTrasactionStatus;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
