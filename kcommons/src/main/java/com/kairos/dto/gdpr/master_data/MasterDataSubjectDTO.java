package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class MasterDataSubjectDTO extends DataSubjectDTO{


    @NotEmpty(message = "error.message.organizationType.not.Selected")
    @Valid
    private List<OrganizationTypeDTO> organizationTypes;

    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    @Valid
    private List<OrganizationSubTypeDTO> organizationSubTypes;
}
