package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by pavan on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffExperienceInExpertiseDTO {
    private Long id;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;


    public StaffExperienceInExpertiseDTO() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Integer getRelevantExperienceInMonths() {
        return relevantExperienceInMonths;
    }

    public void setRelevantExperienceInMonths(Integer relevantExperienceInMonths) {
        this.relevantExperienceInMonths = relevantExperienceInMonths;
    }


}
