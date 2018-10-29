package com.kairos.persistence.model.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.FlexibleTimeDetails;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class FlexibleTimeSettings extends MongoBaseEntity {
    private short timeLimit;
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;
    private Long countryId;

    public FlexibleTimeSettings() {
        //Default Constructor
    }

    public FlexibleTimeSettings(BigInteger id,FlexibleTimeDetails flexibleTimeForCheckIn, FlexibleTimeDetails flexibleTimeForCheckOut, short timeLimit) {
        this.id=id;
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
        this.timeLimit=timeLimit;
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

    public short getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(short timeLimit) {
        this.timeLimit = timeLimit;
    }
}
