package com.kairos.persistence.model.timetype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by vipul on 13/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceTypeWithTimeTypeDTO {
    private List<PresenceTypeDTO> presenceTypes;
    private List<TimeTypeDTO> timeTypes;

    public List<PresenceTypeDTO> getPresenceTypes() {
        return presenceTypes;
    }

    public void setPresenceTypes(List<PresenceTypeDTO> presenceTypes) {
        this.presenceTypes = presenceTypes;
    }

    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }
}
