package com.kairos.activity.persistence.model.activity;


import com.kairos.activity.enums.TimeTypes;
import com.kairos.activity.persistence.enums.task_type.TaskTypeEnum;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "time_Type")
public class TimeType extends MongoBaseEntity{

    private Long countryId;
    private TimeTypes timeTypes;
    private BigInteger upperLevelTimeTypeId;
    private String label;
    private String description;


    public TimeType(BigInteger upperLevelTimeTypeId, String label, String description) {
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
        this.label = label;
        this.description = description;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public TimeType() {}

    public TimeType(TimeTypes timeTypes, String label, String description) {
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
    }

    public TimeTypes getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(TimeTypes timeTypes) {
        this.timeTypes = timeTypes;
    }

    public BigInteger getUpperLevelTimeTypeId() {
        return upperLevelTimeTypeId;
    }

    public void setUpperLevelTimeTypeId(BigInteger upperLevelTimeTypeId) {
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
