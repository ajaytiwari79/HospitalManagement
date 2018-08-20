package com.kairos.persistence.model.open_shift;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.*;

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
    private List<Long> assignedStaff;

    public OpenShift() {
        //Default Constructor
    }

    public OpenShift(BigInteger id, Date startDate, Date endDate, Integer noOfPersonRequired, List<Long> interestedStaff, List<Long> declinedBy, Long unitId, BigInteger parentOpenShiftId) {
        this.id=id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfPersonRequired = noOfPersonRequired;
        this.interestedStaff = interestedStaff;
        this.declinedBy = declinedBy;
        this.unitId = unitId;
    }

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
        return interestedStaff=Optional.ofNullable(interestedStaff).orElse(new ArrayList<>());
    }

    public void setInterestedStaff(List<Long> interestedStaff) {
        this.interestedStaff = interestedStaff;
    }

    public List<Long> getDeclinedBy() {
        return declinedBy=Optional.ofNullable(declinedBy).orElse(new ArrayList<>());
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

    public List<Long> getAssignedStaff() {
        return assignedStaff=Optional.ofNullable(assignedStaff).orElse(new ArrayList<>());
    }

    public void setAssignedStaff(List<Long> assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenShift openShift = (OpenShift) o;
        return Objects.equals(id, openShift.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
