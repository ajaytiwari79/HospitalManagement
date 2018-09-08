package com.kairos.dto.activity.open_shift;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OpenShiftDTO {
    private BigInteger id;
    private Date startDate;
    private Date endDate;
    private Integer noOfPersonRequired;
    private List<Long> interestedStaff;
    private List<Long> declinedBy;
    private Long unitId;
    private BigInteger orderId;
    private BigInteger activityId;
    private List<Long> assignedStaff;

    public OpenShiftDTO() {
        //Default Constructor
    }

    public OpenShiftDTO(Date startDate, Date endDate, Integer noOfPersonRequired, List<Long> interestedStaff,
                        List<Long> declinedBy, Long unitId, BigInteger orderId, BigInteger activityId, BigInteger parentOpenShiftId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfPersonRequired = noOfPersonRequired;
        this.interestedStaff = interestedStaff;
        this.declinedBy = declinedBy;
        this.unitId = unitId;
        this.orderId = orderId;
        this.activityId = activityId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<Long> getAssignedStaff() {
        return assignedStaff=Optional.ofNullable(assignedStaff).orElse(new ArrayList<>());
    }

    public void setAssignedStaff(List<Long> assignedStaff) {
        this.assignedStaff = assignedStaff;
    }
}
