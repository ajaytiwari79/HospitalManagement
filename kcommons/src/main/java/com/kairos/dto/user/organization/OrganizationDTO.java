package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.address.AddressDTO;
import com.kairos.enums.OrganizationLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrganizationDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isPreKairos;
    private OrganizationTypeDTO organizationType;
    private List<OrganizationTypeDTO> organizationSubTypes;
    private List<Long> businessTypeId;
    private AddressDTO contactAddress;
    private int dayShiftTimeDeduction = 4; //in percentage

    private int nightShiftTimeDeduction = 7; //in percentage
    private OrganizationLevel organizationLevel = OrganizationLevel.CITY;
    private boolean isOneTimeSyncPerformed;
    private Long countryId;
    private boolean isParentOrganization;
    private List<TagDTO> tagDTOS;

}


