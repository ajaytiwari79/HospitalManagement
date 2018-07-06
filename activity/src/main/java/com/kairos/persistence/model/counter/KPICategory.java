package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 * @usage: categories for KPIs.
 */

public class KPICategory extends MongoBaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
