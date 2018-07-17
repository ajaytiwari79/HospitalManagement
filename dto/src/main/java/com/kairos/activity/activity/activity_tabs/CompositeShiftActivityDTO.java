package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeShiftActivityDTO {

    private BigInteger activityId;
    private Set<BigInteger> restrictedActivitiesBefore;
    private Set<BigInteger> restrictedActivitiesAfter;
    public CompositeShiftActivityDTO(){
        // DC
    }
    public BigInteger getActivityId(){
        return this.activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Set<BigInteger> getRestrictedActivitiesBefore() {
        return restrictedActivitiesBefore=Optional.ofNullable(restrictedActivitiesBefore).orElse(new HashSet<>()) ;
    }

    public void setRestrictedActivitiesBefore(Set<BigInteger> restrictedActivitiesBefore) {
        this.restrictedActivitiesBefore = restrictedActivitiesBefore;
    }

    public Set<BigInteger> getRestrictedActivitiesAfter() {
        return restrictedActivitiesAfter=Optional.ofNullable(restrictedActivitiesAfter).orElse(new HashSet<>()) ;
    }

    public void setRestrictedActivitiesAfter(Set<BigInteger> restrictedActivitiesAfter) {
        this.restrictedActivitiesAfter = restrictedActivitiesAfter;
    }
}
