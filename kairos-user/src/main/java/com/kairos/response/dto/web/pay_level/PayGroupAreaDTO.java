package com.kairos.response.dto.web.pay_level;

import com.kairos.persistence.model.user.pay_level.FutureDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 * Created by prabjot on 21/12/17.
 */
public class PayGroupAreaDTO {

    @NotNull(message = "Name can not be null")
    private String name;
    private String description;
    @Size(min = 0,message = "Please select mun")
    private Set<Long> municipalityId;
    @FutureOrPresent
    private Long startDateMillis;
    private Long endDateMillis;


    public PayGroupAreaDTO() {
        //default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Set<Long> municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }
}
