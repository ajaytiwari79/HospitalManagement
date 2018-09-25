package com.kairos.dto.activity.planned_time_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by vipul on 7/12/17.
 * updation by Mohit Shakya on Jun/05/2018
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

}
