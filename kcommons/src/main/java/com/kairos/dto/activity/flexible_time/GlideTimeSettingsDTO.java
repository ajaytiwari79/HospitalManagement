package com.kairos.dto.activity.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;

public class GlideTimeSettingsDTO {
    private BigInteger id;
    private short timeLimit;
    private GlideTimeDetails glideTimeForCheckIn;
    private GlideTimeDetails glideTimeForCheckOut;
    private Long countryId;

    public GlideTimeSettingsDTO() {
        //Default Constructor
    }

    public GlideTimeSettingsDTO(BigInteger id, GlideTimeDetails glideTimeForCheckIn, GlideTimeDetails glideTimeForCheckOut, Long countryId) {
        this.id = id;
        this.glideTimeForCheckIn = glideTimeForCheckIn;
        this.glideTimeForCheckOut = glideTimeForCheckOut;
        this.countryId = countryId;
    }
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    @AssertTrue(message = "error.flexi_time.exceeds.limit")
    public boolean isValid() {
        return (glideTimeForCheckIn.getAfter()!=null && timeLimit>= glideTimeForCheckIn.getAfter() ||
                glideTimeForCheckIn.getBefore()!=null && timeLimit>= glideTimeForCheckIn.getBefore()||
                glideTimeForCheckOut.getAfter()!=null && timeLimit>= glideTimeForCheckOut.getAfter() ||
                glideTimeForCheckOut.getBefore()!=null && timeLimit>= glideTimeForCheckIn.getBefore());
    }

}
