package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CategoryKPIConf extends MongoBaseEntity {
    private BigInteger kpiId;
    private BigInteger categoryId;

    public CategoryKPIConf() {
    }

    public CategoryKPIConf(BigInteger kpiAssignmentId, BigInteger categoryAssignmentId){
        this.categoryId = categoryAssignmentId;
        this.kpiId = kpiAssignmentId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

}
