package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.enums.LocationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLocationSettings implements Serializable {
    private static final long serialVersionUID = -5767089276245635842L;
    @KPermissionField
    private Set<ActivityGlideTimeDetails> glideTimeForCheckIn;
    @KPermissionField
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
