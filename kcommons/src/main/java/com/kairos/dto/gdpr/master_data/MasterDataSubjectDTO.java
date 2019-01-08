package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterDataSubjectDTO extends DataSubjectDTO{


    @NotEmpty(message = "error.message.organizationType.not.Selected")
    @Valid
    private List<OrganizationTypeDTO> organizationTypeDTOS;

    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    @Valid
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() { return organizationTypeDTOS; }

    public void setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) { this.organizationTypeDTOS = organizationTypeDTOS; }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() { return organizationSubTypeDTOS; }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) { this.organizationSubTypeDTOS = organizationSubTypeDTOS; }
}
