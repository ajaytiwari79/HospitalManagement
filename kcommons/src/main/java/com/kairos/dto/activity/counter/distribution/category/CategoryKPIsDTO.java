package com.kairos.dto.activity.counter.distribution.category;

import java.math.BigInteger;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 *
 */

public class CategoryKPIsDTO {
    private BigInteger categoryId;
    private List<BigInteger> kpiIds;

    public CategoryKPIsDTO(){
    }

    public CategoryKPIsDTO(BigInteger categoryId, List<BigInteger> kpiIds) {
        this.categoryId = categoryId;
        this.kpiIds = kpiIds;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
