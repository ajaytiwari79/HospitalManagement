package com.kairos.shiftplanning.domain.activityConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ActivityConstraints {

    private LongestDuration longestDuration;
    private ShortestDuration shortestDuration;
    private MaxAllocationPerShift maxAllocationPerShift;
    private ContinousActivityPerShift continousActivityPerShift;
    private MaxDiffrentActivity maxDiffrentActivity;
    private MinimumLengthofActivity minimumLengthofActivity;
    private ActivityDayType activityDayType;//Activity is used on a DayType


    public ActivityConstraints(LongestDuration longestDuration, ShortestDuration shortestDuration, MaxAllocationPerShift maxAllocationPerShift,MaxDiffrentActivity maxDiffrentActivity,MinimumLengthofActivity minimumLengthofActivity,ActivityDayType activityDayType) {
        this.longestDuration = longestDuration;
        this.shortestDuration = shortestDuration;
        this.maxAllocationPerShift = maxAllocationPerShift;
        //this.continousActivityPerShift = continousActivityPerShift;
        this.maxDiffrentActivity = maxDiffrentActivity;
        this.minimumLengthofActivity = minimumLengthofActivity;
        this.activityDayType = activityDayType;
    }

    public ActivityDayType getActivityDayType() {
        return activityDayType;
    }

    public void setActivityDayType(ActivityDayType activityDayType) {
        this.activityDayType = activityDayType;
    }

    public MaxDiffrentActivity getMaxDiffrentActivity() {
        return maxDiffrentActivity;
    }

    public void setMaxDiffrentActivity(MaxDiffrentActivity maxDiffrentActivity) {
        this.maxDiffrentActivity = maxDiffrentActivity;
    }

    public ActivityConstraints() {
    }

    public MinimumLengthofActivity getMinimumLengthofActivity() {
        return minimumLengthofActivity;
    }

    public void setMinimumLengthofActivity(MinimumLengthofActivity minimumLengthofActivity) {
        this.minimumLengthofActivity = minimumLengthofActivity;
    }

    public ContinousActivityPerShift getContinousActivityPerShift() {
        return continousActivityPerShift;
    }

    public void setContinousActivityPerShift(ContinousActivityPerShift continousActivityPerShift) {
        this.continousActivityPerShift = continousActivityPerShift;
    }

    public LongestDuration getLongestDuration() {
        return longestDuration;
    }

    public void setLongestDuration(LongestDuration longestDuration) {
        this.longestDuration = longestDuration;
    }

    public ShortestDuration getShortestDuration() {
        return shortestDuration;
    }

    public void setShortestDuration(ShortestDuration shortestDuration) {
        this.shortestDuration = shortestDuration;
    }

    public MaxAllocationPerShift getMaxAllocationPerShift() {
        return maxAllocationPerShift;
    }

    public void setMaxAllocationPerShift(MaxAllocationPerShift maxAllocationPerShift) {
        this.maxAllocationPerShift = maxAllocationPerShift;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ActivityConstraints that = (ActivityConstraints) o;

        return new EqualsBuilder()
                .append(longestDuration, that.longestDuration)
                .append(shortestDuration, that.shortestDuration)
                .append(maxAllocationPerShift, that.maxAllocationPerShift)
                .append(continousActivityPerShift, that.continousActivityPerShift)
                .append(maxDiffrentActivity, that.maxDiffrentActivity)
                .append(minimumLengthofActivity, that.minimumLengthofActivity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(longestDuration)
                .append(shortestDuration)
                .append(maxAllocationPerShift)
                .append(continousActivityPerShift)
                .append(maxDiffrentActivity)
                .append(minimumLengthofActivity)
                .toHashCode();
    }
}
