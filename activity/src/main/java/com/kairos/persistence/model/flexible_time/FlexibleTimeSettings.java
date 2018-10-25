package com.kairos.persistence.model.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.persistence.model.common.MongoBaseEntity;

public class FlexibleTimeSettings extends MongoBaseEntity {
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;
    private Long countryId;

    public FlexibleTimeSettings() {
        //Default Constructor
    }

    public FlexibleTimeDetails getFlexibleTimeForCheckIn() {
        return flexibleTimeForCheckIn;
    }

    public void setFlexibleTimeForCheckIn(FlexibleTimeDetails flexibleTimeForCheckIn) {
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
    }

    public FlexibleTimeDetails getFlexibleTimeForCheckOut() {
        return flexibleTimeForCheckOut;
    }

    public void setFlexibleTimeForCheckOut(FlexibleTimeDetails flexibleTimeForCheckOut) {
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

}
