package com.kairos.dto.activity.open_shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivitiesPerTimeType {
    private BigInteger timeTypeId;
    private String timeTypeName;
    private List<BigInteger> selectedActivities;
    private boolean selected;

    public ActivitiesPerTimeType() {
        //Default Constructor
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public String getTimeTypeName() {
        return timeTypeName;
    }

    public void setTimeTypeName(String timeTypeName) {
        this.timeTypeName = timeTypeName;
    }

    public List<BigInteger> getSelectedActivities() {
        return selectedActivities;
    }

    public void setSelectedActivities(List<BigInteger> selectedActivities) {
        this.selectedActivities = selectedActivities;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
