package com.kairos.response.dto.web.open_shift;

import java.math.BigInteger;
import java.util.List;

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
