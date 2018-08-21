package com.kairos.activity.counter.distribution.category;

import java.math.BigInteger;
import java.util.List;

public class CategoryKPIMappingDTO {
    private BigInteger categoryId;
    private List<BigInteger> kpiId;

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public List<BigInteger> getKpiId() {
        return kpiId;
    }

    public void setKpiId(List<BigInteger> kpiId) {
        this.kpiId = kpiId;
    }
}
