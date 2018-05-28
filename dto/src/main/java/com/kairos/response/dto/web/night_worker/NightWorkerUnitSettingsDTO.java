package com.kairos.response.dto.web.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NightWorkerUnitSettingsDTO {

    private Integer eligibleMinAge;
    private Integer eligibleMaxAge;

    public NightWorkerUnitSettingsDTO(){
        // default constructor
    }

    public Integer getEligibleMinAge() {
        return eligibleMinAge;
    }

    public void setEligibleMinAge(Integer eligibleMinAge) {
        this.eligibleMinAge = eligibleMinAge;
    }

    public Integer getEligibleMaxAge() {
        return eligibleMaxAge;
    }

    public void setEligibleMaxAge(Integer eligibleMaxAge) {
        this.eligibleMaxAge = eligibleMaxAge;
    }
}
