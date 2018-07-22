package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 * @usage: categories for KPIs.
 */

public class KPICategory extends MongoBaseEntity {
    private String name;
    private Long levelId;   //levelId is country/unit id
    private ConfLevel ownerLevel; //it identifies it has been created by country / unit

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

    public ConfLevel getOwnerLevel() {
        return ownerLevel;
    }

    public void setOwnerLevel(ConfLevel ownerLevel) {
        this.ownerLevel = ownerLevel;
    }
}
