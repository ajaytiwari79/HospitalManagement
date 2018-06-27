package com.kairos.activity.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.OrganizationDTO;
import com.kairos.persistence.model.country.CountryDTO;
import com.kairos.activity.web.OrganizationTypeDTO;
import com.kairos.persistence.model.country.experties.ExpertiseResponseDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 11/4/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTABasicDetailsDTO {

    private ExpertiseResponseDTO expertiseResponse;
    private OrganizationTypeDTO organizationType;
    private OrganizationDTO organization;
    private List<OrganizationDTO> organizations;
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

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public List<OrganizationDTO> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationDTO> organizations) {
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
