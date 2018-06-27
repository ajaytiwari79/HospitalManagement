package com.kairos.activity.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jasgeet on 12/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationExternalIdsDTO {
    private String kmdExternalId;
    private String timeCareExternalId;

    public String getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(String kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public String getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(String timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }
}
