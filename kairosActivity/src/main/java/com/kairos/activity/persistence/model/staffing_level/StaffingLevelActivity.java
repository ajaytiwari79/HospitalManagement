package com.kairos.activity.persistence.model.staffing_level;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class StaffingLevelActivity {

    private String name;
    private Long activityId;
    private int noOfStaff;
    private boolean includeInMin;

    public StaffingLevelActivity() {
        //default constructor
    }

    public StaffingLevelActivity(String name, int noOfStaff) {
        this.name = name;
        this.noOfStaff = noOfStaff;
    }

    public StaffingLevelActivity(Long activityId, Integer noOfStaff) {
        this.activityId = activityId;
        this.noOfStaff = noOfStaff;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public int getNoOfStaff() {
        return noOfStaff;
    }

    public void setNoOfStaff(int noOfStaff) {
        this.noOfStaff = noOfStaff;
    }

    public boolean isIncludeInMin() {
        return includeInMin;
    }

    public void setIncludeInMin(boolean includeInMin) {
        this.includeInMin = includeInMin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelActivity)) return false;

        StaffingLevelActivity that = (StaffingLevelActivity) o;

        return new EqualsBuilder()
                .append(activityId, that.activityId)
                .append(noOfStaff, that.noOfStaff)
                .isEquals();
    }

    /*@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(activityId)
                .append(noOfStaff)
                .toHashCode();
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(name, noOfStaff);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("activityId", activityId)
                .append("noOfStaff", noOfStaff)
                .toString();
    }
}
