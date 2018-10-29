package com.kairos.persistence.model.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.GlideTimeDetails;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class GlideTimeSettings extends MongoBaseEntity {
    private short timeLimit;
    private GlideTimeDetails flexibleTimeForCheckIn;
    private GlideTimeDetails flexibleTimeForCheckOut;
    private Long countryId;

    public GlideTimeSettings() {
        //Default Constructor
    }

    public GlideTimeSettings(BigInteger id, GlideTimeDetails flexibleTimeForCheckIn, GlideTimeDetails flexibleTimeForCheckOut, short timeLimit) {
        this.id=id;
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
        this.timeLimit=timeLimit;
    }

    public GlideTimeDetails getFlexibleTimeForCheckIn() {
        return flexibleTimeForCheckIn;
    }

    public void setFlexibleTimeForCheckIn(GlideTimeDetails flexibleTimeForCheckIn) {
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
    }

    public GlideTimeDetails getFlexibleTimeForCheckOut() {
        return flexibleTimeForCheckOut;
    }

    public void setFlexibleTimeForCheckOut(GlideTimeDetails flexibleTimeForCheckOut) {
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
