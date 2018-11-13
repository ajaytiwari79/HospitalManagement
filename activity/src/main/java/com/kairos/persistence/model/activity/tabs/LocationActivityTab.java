package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.enums.LocationEnum;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
public class LocationActivityTab implements Serializable {
    private Set<ActivityGlideTimeDetails> glideTimeForCheckIn;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckOut;



    public LocationActivityTab() {
        //Default Constructor
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

    public LocationActivityTab(Set<ActivityGlideTimeDetails> glideTimeForCheckIn, Set<ActivityGlideTimeDetails> glideTimeForCheckOut) {
        this.glideTimeForCheckIn = glideTimeForCheckIn;
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
