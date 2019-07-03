package com.kairos.response.dto.policy_agreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PolicyAgreementTemplateResponseDTO {

    private Long id;
    private String name;
    private String description;
    private List<AccountTypeVO> accountTypes= new ArrayList<>();
    private List<OrganizationTypeDTO> organizationTypes=new ArrayList<>();
    private List<OrganizationSubTypeDTO> organizationSubTypes=new ArrayList<>();
    private List<ServiceCategoryDTO> organizationServices=new ArrayList<>();
    private List<SubServiceCategoryDTO> organizationSubServices=new ArrayList<>();
    private TemplateTypeResponseDTO templateType;
    private boolean generalAgreementTemplate;

    public PolicyAgreementTemplateResponseDTO(Long id, String name, String description, TemplateTypeResponseDTO templateType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.templateType = templateType;
    }
}
