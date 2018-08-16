package com.kairos.activity.counter.distribution.category;

import java.math.BigInteger;

public class CategoryKPIConfDTO {
    private BigInteger categoryAssignmentId;
    private BigInteger kpiAssignmentId;
    private BigInteger categoryId;
    private BigInteger kpiId;

    public CategoryKPIConfDTO(){

    }

    public BigInteger getCategoryAssignmentId() {
        return categoryAssignmentId;
    }

    public void setCategoryAssignmentId(BigInteger categoryAssignmentId) {
        this.categoryAssignmentId = categoryAssignmentId;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }
}
