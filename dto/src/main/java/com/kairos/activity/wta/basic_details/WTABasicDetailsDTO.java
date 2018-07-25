package com.kairos.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.kairos.user.country.basic_details.CountryDTO;
import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.user.organization.OrganizationBasicDTO;
import com.kairos.user.organization.OrganizationTypeDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 11/4/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTABasicDetailsDTO {

    private ExpertiseResponseDTO expertiseResponse;
    private OrganizationTypeDTO organizationType;
    private OrganizationBasicDTO organization;
    private List<OrganizationBasicDTO> organizations;
    private OrganizationTypeDTO organizationSubType;
    private CountryDTO countryDTO;

    public ExpertiseResponseDTO getExpertiseResponse() {
        return expertiseResponse;
    }

    public void setExpertiseResponse(ExpertiseResponseDTO expertiseResponse) {
        this.expertiseResponse = expertiseResponse;
    }

    public OrganizationTypeDTO getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeDTO organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationBasicDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationBasicDTO organization) {
        this.organization = organization;
    }

    public List<OrganizationBasicDTO> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationBasicDTO> organizations) {
        this.organizations = organizations;
    }

    public OrganizationTypeDTO getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationTypeDTO organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public CountryDTO getCountryDTO() {
        return countryDTO;
    }

    public void setCountryDTO(CountryDTO countryDTO) {
        this.countryDTO = countryDTO;
    }
}
