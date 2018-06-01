package com.kairos.activity.client.dto.activityType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.wta.PresenceTypeDTO;

import java.util.List;

/**
 * Created by vipul on 7/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceTypeWithTimeTypeDTO {
    private List<PresenceTypeDTO> presenceTypes;
    private Long countryId;


    public PresenceTypeWithTimeTypeDTO() {
    }

    public PresenceTypeWithTimeTypeDTO(List<PresenceTypeDTO> presenceTypes, Long countryId) {
        this.presenceTypes = presenceTypes;
        this.countryId = countryId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<PresenceTypeDTO> getPresenceTypes() {
        return presenceTypes;
    }

    public void setPresenceTypes(List<PresenceTypeDTO> presenceTypes) {
        this.presenceTypes = presenceTypes;
    }

/*
    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }
*/
}
