package com.kairos.dto.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 11/4/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WTABasicDetailsDTO {

    private ExpertiseResponseDTO expertiseResponse;
    private OrganizationTypeDTO organizationType;
    private OrganizationBasicDTO organization;
    private List<OrganizationBasicDTO> organizations;
    private OrganizationTypeDTO organizationSubType;
    private CountryDTO countryDTO;

    public List<OrganizationBasicDTO> getOrganizations() {
        return isNullOrElse(organizations,new ArrayList<>());
    }

}
