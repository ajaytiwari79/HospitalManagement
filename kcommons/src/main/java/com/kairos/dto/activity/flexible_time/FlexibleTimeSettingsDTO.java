package com.kairos.dto.activity.flexible_time;/*
 *Created By Pavan on 20/10/18
 *
 */

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;

public class FlexibleTimeSettingsDTO {
    private BigInteger id;
    private short timeLimit;
    private FlexibleTimeDetails flexibleTimeForCheckIn;
    private FlexibleTimeDetails flexibleTimeForCheckOut;
    private Long countryId;

    public FlexibleTimeSettingsDTO() {
        //Default Constructor
    }

    public FlexibleTimeSettingsDTO(BigInteger id, FlexibleTimeDetails flexibleTimeForCheckIn, FlexibleTimeDetails flexibleTimeForCheckOut, Long countryId) {
        this.id = id;
        this.flexibleTimeForCheckIn = flexibleTimeForCheckIn;
        this.flexibleTimeForCheckOut = flexibleTimeForCheckOut;
        this.countryId = countryId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    @AssertTrue(message = "All times should be equal or greater than time limit")
    public boolean isValid() {
        return (flexibleTimeForCheckIn.getAfter()!=null && timeLimit>=flexibleTimeForCheckIn.getAfter() ||
                flexibleTimeForCheckIn.getBefore()!=null && timeLimit>=flexibleTimeForCheckIn.getBefore()||
                flexibleTimeForCheckOut.getAfter()!=null && timeLimit>=flexibleTimeForCheckOut.getAfter() ||
                flexibleTimeForCheckOut.getBefore()!=null && timeLimit>=flexibleTimeForCheckIn.getBefore());
    }

}
