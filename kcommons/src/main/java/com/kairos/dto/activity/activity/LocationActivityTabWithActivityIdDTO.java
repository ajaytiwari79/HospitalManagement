package com.kairos.dto.activity.activity;

import com.kairos.dto.activity.activity.activity_tabs.LocationActivityTabDTO;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 1/11/18
 */

public class LocationActivityTabWithActivityIdDTO {
    private BigInteger id;
    private LocationActivityTabDTO locationActivityTab;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public LocationActivityTabDTO getLocationActivityTab() {
        return locationActivityTab;
    }

    public void setLocationActivityTab(LocationActivityTabDTO locationActivityTab) {
        this.locationActivityTab = locationActivityTab;
    }
}
