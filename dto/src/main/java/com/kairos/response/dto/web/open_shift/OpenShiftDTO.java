package com.kairos.response.dto.web.open_shift;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

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
    private BigInteger parentOpenShiftId;

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
        this.parentOpenShiftId = parentOpenShiftId;
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

    public BigInteger getParentOpenShiftId() {
        return parentOpenShiftId;
    }

    public void setParentOpenShiftId(BigInteger parentOpenShiftId) {
        this.parentOpenShiftId = parentOpenShiftId;
    }
}
