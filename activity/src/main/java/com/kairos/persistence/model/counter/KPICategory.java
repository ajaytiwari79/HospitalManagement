package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 * @usage: categories for KPIs.
 */

public class KPICategory extends MongoBaseEntity {
    private String name;
    private Long levelId;   //levelId is country/unit id
    private BigInteger parentCategoryId;

    public KPICategory(){}

    public KPICategory(String name, Long levelId,BigInteger parentCategoryId){
        this.name = name;
        this.levelId = levelId;
        this.parentCategoryId=parentCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public BigInteger getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(BigInteger parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
