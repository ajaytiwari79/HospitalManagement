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
    private List<OrganizationTypeDTO> organizationTypes;

    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    @Valid
    private List<OrganizationSubTypeDTO> organizationSubTypes;

    public List<OrganizationTypeDTO> getOrganizationTypes() { return organizationTypes; }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) { this.organizationTypes = organizationTypes; }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() { return organizationSubTypes; }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }
}
