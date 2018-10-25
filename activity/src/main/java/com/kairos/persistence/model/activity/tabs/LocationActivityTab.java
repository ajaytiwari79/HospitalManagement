package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.flexible_time.ActivityFlexibleTimeDetails;
import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.enums.LocationEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
public class LocationActivityTab implements Serializable {
    private Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckIn;
    private Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckOut;



    public LocationActivityTab() {
        //Default Constructor
    }

    public Set<ActivityFlexibleTimeDetails> getFlexibleTimeForCheckIn() {
        return flexibleTimeForCheckIn;
    }

    public void setFlexibleTimeForCheckIn(Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckIn) {
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
    }

    public Set<ActivityFlexibleTimeDetails> getFlexibleTimeForCheckOut() {
        return flexibleTimeForCheckOut;
    }

    public void setFlexibleTimeForCheckOut(Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckOut) {
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
    }

    public LocationActivityTab(Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckIn, Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckOut) {
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
    }
}
