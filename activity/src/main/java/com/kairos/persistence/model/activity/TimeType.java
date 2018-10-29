package com.kairos.persistence.model.activity;


import com.kairos.enums.TimeTypes;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "time_Type")
public class TimeType extends MongoBaseEntity{

    private Long countryId;
    private TimeTypes timeTypes;
    private BigInteger upperLevelTimeTypeId;
    private String label;
    private boolean leafNode;
    private String description;
    private List<BigInteger> childTimeTypeIds = new ArrayList<>();
    private String backgroundColor;
    private BreakPaymentSetting breakPaymentSetting;
    public TimeType() {}
    public TimeType(BigInteger upperLevelTimeTypeId, String label, String description) {
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
        this.label = label;
        this.description = description;
    }



    public TimeType(TimeTypes timeTypes, String label, String description,String backgroundColor) {
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
        this.backgroundColor=backgroundColor;
        this.leafNode = true;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public List<BigInteger> getChildTimeTypeIds() {
        return childTimeTypeIds;
    }

    public void setChildTimeTypeIds(List<BigInteger> childTimeTypeIds) {
        this.childTimeTypeIds = childTimeTypeIds;
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

    public boolean isLeafNode() {
        return leafNode;
    }

    public void setLeafNode(boolean leafNode) {
        this.leafNode = leafNode;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }
}
