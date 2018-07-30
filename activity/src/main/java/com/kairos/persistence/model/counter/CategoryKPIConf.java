package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CategoryKPIConf extends MongoBaseEntity {
    private BigInteger kpiAssignmentId;
    private BigInteger categoryAssignmentId;

    public CategoryKPIConf(BigInteger kpiAssignmentId, BigInteger categoryAssignmentId){
        this.categoryAssignmentId = categoryAssignmentId;
        this.kpiAssignmentId = kpiAssignmentId;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }

    public BigInteger getCategoryAssignmentId() {
        return categoryAssignmentId;
    }

    public void setCategoryAssignmentId(BigInteger categoryAssignmentId) {
        this.categoryAssignmentId = categoryAssignmentId;
    }

}
