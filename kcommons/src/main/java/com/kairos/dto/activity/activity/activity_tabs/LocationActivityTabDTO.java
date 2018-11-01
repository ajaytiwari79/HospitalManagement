package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.enums.LocationEnum;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationActivityTabDTO {
    private BigInteger activityId;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckIn;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckOut;

    public LocationActivityTabDTO() {
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Set<ActivityGlideTimeDetails> getGlideTimeForCheckIn() {
        return glideTimeForCheckIn;
    }

    public void setGlideTimeForCheckIn(Set<ActivityGlideTimeDetails> glideTimeForCheckIn) {
        this.glideTimeForCheckIn = glideTimeForCheckIn;
    }

    public Set<ActivityGlideTimeDetails> getGlideTimeForCheckOut() {
        return glideTimeForCheckOut;
    }

    public void setGlideTimeForCheckOut(Set<ActivityGlideTimeDetails> glideTimeForCheckOut) {
        this.glideTimeForCheckOut = glideTimeForCheckOut;
    }

    public ActivityGlideTimeDetails getCheckInGlideTime(LocationEnum locationEnum){
        ActivityGlideTimeDetails activityGlideTimeDetails = null;
        for (ActivityGlideTimeDetails glideTimeDetails : glideTimeForCheckIn) {
            if(locationEnum.equals(glideTimeDetails.getLocation())){
                activityGlideTimeDetails = glideTimeDetails;
            }
        }
        return activityGlideTimeDetails;
    }

    public ActivityGlideTimeDetails getCheckOutGlideTime(LocationEnum locationEnum){
        ActivityGlideTimeDetails activityGlideTimeDetails = null;
        for (ActivityGlideTimeDetails glideTimeDetails : glideTimeForCheckOut) {
            if(locationEnum.equals(glideTimeDetails.getLocation())){
                activityGlideTimeDetails = glideTimeDetails;
            }
        }
        return activityGlideTimeDetails;
    }

}
