package com.kairos.response.dto.policy_agreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class GeneralAgreementTemplateResponseDTO {
    private Long id;
    private String name;
    private String description;
    private TemplateTypeResponseDTO templateType;
    private boolean generalAgreementTemplate;
    private List<AgreementSectionResponseDTO> sections=new ArrayList<>();

    public GeneralAgreementTemplateResponseDTO(Long id, String name, String description, TemplateTypeResponseDTO templateType, boolean generalAgreementTemplate, List<AgreementSectionResponseDTO> sections) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.templateType = templateType;
        this.generalAgreementTemplate = generalAgreementTemplate;
        this.sections = sections;
    }
}
