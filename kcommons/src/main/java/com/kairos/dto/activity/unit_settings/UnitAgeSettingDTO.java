package com.kairos.dto.activity.unit_settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitAgeSettingDTO {

    private Integer younger;
    private Integer older;

    public UnitAgeSettingDTO(){
        // default constructor
    }

    public Integer getYounger() {
        return younger;
    }

    public void setYounger(Integer younger) {
        this.younger = younger;
    }

    public Integer getOlder() {
        return older;
    }

    public void setOlder(Integer older) {
        this.older = older;
    }
}
