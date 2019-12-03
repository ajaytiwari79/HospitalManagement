package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.enums.LocationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationActivityTab{
    private Set<ActivityGlideTimeDetails> glideTimeForCheckIn;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckOut;

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
