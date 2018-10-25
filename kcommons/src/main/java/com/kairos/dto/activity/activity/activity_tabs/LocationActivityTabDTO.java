package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.flexible_time.ActivityFlexibleTimeDetails;
import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.enums.LocationEnum;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationActivityTabDTO {
    private BigInteger activityId;
    private Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckIn;
    private Set<ActivityFlexibleTimeDetails> flexibleTimeForCheckOut;

    public LocationActivityTabDTO() {
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
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
}
