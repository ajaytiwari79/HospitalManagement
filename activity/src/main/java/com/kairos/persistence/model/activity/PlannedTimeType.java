package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;

/*
 * @author: Mohit Shakya
 * @usage: This domain is for category Planned time type.
 *
 */
public class PlannedTimeType extends MongoBaseEntity {
    private String name;
    private Long countryId;
    private String imageName;

    public PlannedTimeType(){

    }
    
    public PlannedTimeType(String name, Long countryId){
        this.name = name;
        this.countryId = countryId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

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
