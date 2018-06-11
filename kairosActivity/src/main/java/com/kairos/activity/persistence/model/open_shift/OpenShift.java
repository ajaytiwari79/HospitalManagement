package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Document
public class OpenShift extends MongoBaseEntity {

    private Date startDate;
    private Date endDate;
    private Integer noOfPersonRequired;
    private List<Long> interestedStaff;
    private List<Long> declinedBy;
    private Long unitId;
    private BigInteger orderId;
    private BigInteger activityId;
    private BigInteger parentOpenShiftId;


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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


    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }
}
