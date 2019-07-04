package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties
@Getter
@Setter
@NoArgsConstructor
public class OrgTypeSubTypeServicesAndSubServicesDTO {

    private Long id;
    private String name;
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;

}
