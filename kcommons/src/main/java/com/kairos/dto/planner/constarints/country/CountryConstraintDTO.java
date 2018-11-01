package com.kairos.dto.planner.constarints.country;

import com.kairos.dto.planner.constarints.ConstraintDTO;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

public class CountryConstraintDTO extends ConstraintDTO {
    //~
    //@NotBlank
    private Long countryId;
   // @NotBlank
    private Long organizationServiceId;
    //@NotBlank
    private Long organizationSubServiceId;

    //======================================================

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public Long getOrganizationServiceId() {
        return organizationServiceId;
    }

    public void setOrganizationServiceId(Long organizationServiceId) {
        this.organizationServiceId = organizationServiceId;
    }

    public Long getOrganizationSubServiceId() {
        return organizationSubServiceId;
    }

    public void setOrganizationSubServiceId(Long organizationSubServiceId) {
        this.organizationSubServiceId = organizationSubServiceId;
    }
}
