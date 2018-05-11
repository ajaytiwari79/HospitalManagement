package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Document
public class OpenShift extends MongoBaseEntity {

    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer noOfPersonRequired;
    private List<Long> interestedStaff;
    private List<Long> declinedBy;
    private Long unitId;
    private BigInteger orderId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }



    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getNoOfPersonRequired() {
        return noOfPersonRequired;
    }

    public void setNoOfPersonRequired(Integer noOfPersonRequired) {
        this.noOfPersonRequired = noOfPersonRequired;
    }

    public List<Long> getInterestedStaff() {
        return interestedStaff;
    }

    public void setInterestedStaff(List<Long> interestedStaff) {
        this.interestedStaff = interestedStaff;
    }

    public List<Long> getDeclinedBy() {
        return declinedBy;
    }

    public void setDeclinedBy(List<Long> declinedBy) {
        this.declinedBy = declinedBy;
    }


}
