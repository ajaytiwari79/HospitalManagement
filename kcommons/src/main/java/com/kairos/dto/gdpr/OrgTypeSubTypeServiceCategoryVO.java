package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties
@Getter
@Setter
@NoArgsConstructor
public class OrgTypeSubTypeServiceCategoryVO {

    private Long id;
    private String name;
    private Long countryId;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
}
