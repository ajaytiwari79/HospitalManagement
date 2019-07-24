package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.*;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.*;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterClauseDTO extends ClauseDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private Set<OrganizationTypeDTO> organizationTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private Set<OrganizationSubTypeDTO> organizationSubTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private Set<ServiceCategoryDTO> organizationServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private Set<SubServiceCategoryDTO> organizationSubServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

}
