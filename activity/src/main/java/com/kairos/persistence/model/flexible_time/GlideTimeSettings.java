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
    private GlideTimeDetails glideTimeForCheckIn;
    private GlideTimeDetails glideTimeForCheckOut;
    private Long countryId;

    public GlideTimeSettings() {
        //Default Constructor
    }

    public GlideTimeSettings(BigInteger id, GlideTimeDetails glideTimeForCheckIn, GlideTimeDetails glideTimeForCheckOut, short timeLimit) {
        this.id=id;
        this.glideTimeForCheckIn = glideTimeForCheckIn;
        this.glideTimeForCheckOut = glideTimeForCheckOut;
        this.timeLimit=timeLimit;
    }

    public GlideTimeDetails getGlideTimeForCheckIn() {
        return glideTimeForCheckIn;
    }

    public void setGlideTimeForCheckIn(GlideTimeDetails glideTimeForCheckIn) {
        this.glideTimeForCheckIn = glideTimeForCheckIn;
    }

    public GlideTimeDetails getGlideTimeForCheckOut() {
        return glideTimeForCheckOut;
    }

    public void setGlideTimeForCheckOut(GlideTimeDetails glideTimeForCheckOut) {
        this.glideTimeForCheckOut = glideTimeForCheckOut;
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
