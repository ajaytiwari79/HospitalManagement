package com.kairos.activity.staffing_level;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.util.Objects;

public class StaffingLevelActivity {

    private String name;
    private BigInteger activityId;
   // private int noOfStaff;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;

    public int getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public void setMinNoOfStaff(int minNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
    }

    public int getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public void setMaxNoOfStaff(int maxNoOfStaff) {
        this.maxNoOfStaff = maxNoOfStaff;
    }


    public StaffingLevelActivity() {
        //default constructor
    }

    public StaffingLevelActivity(String name, int minNoOfStaff, int maxNoOfStaff) {
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(BigInteger activityId, String name, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

//    public int getNoOfStaff() {
//        return noOfStaff;
//    }
//
//    public void setNoOfStaff(int noOfStaff) {
//        this.noOfStaff = noOfStaff;
//    }

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
                .append(minNoOfStaff, that.minNoOfStaff)
                .append(maxNoOfStaff, that.maxNoOfStaff)
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
        return Objects.hash(activityId,name, minNoOfStaff,maxNoOfStaff);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("activityId", activityId)
                .append("minNoOfStaff", minNoOfStaff)
                .append("maxNoOfStaff",maxNoOfStaff)
                .toString();
    }



}
