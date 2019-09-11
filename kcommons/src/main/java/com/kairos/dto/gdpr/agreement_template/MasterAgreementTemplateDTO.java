package com.kairos.dto.gdpr.agreement_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterAgreementTemplateDTO extends AgreementTemplateDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private List<OrganizationTypeDTO> organizationTypes = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private List<OrganizationSubTypeDTO> organizationSubTypes = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private List<ServiceCategoryDTO> organizationServices = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private List<SubServiceCategoryDTO> organizationSubServices = new ArrayList<>();


    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

}
