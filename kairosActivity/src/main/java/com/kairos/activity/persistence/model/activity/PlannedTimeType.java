package com.kairos.activity.persistence.model.activity;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class PlannedTimeType extends MongoBaseEntity {
    private String name;
    private Long countryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    @Override
    public String toString() {
        return "PlannedTimeType{" +
                "name='" + name + '\'' +
                ", deleted=" + deleted +
                '}';
    }

}
