package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.QuestionnaireAssetTypeDTO;
import com.kairos.enums.gdpr.QuestionType;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireTemplateResponseDTO {


    private Long id;
    private String name;
    private String description;
    private QuestionnaireTemplateType templateType;
    private boolean isDefaultAssetTemplate;
    private QuestionnaireAssetTypeDTO assetType;
    private QuestionnaireAssetTypeDTO subAssetType;
    private QuestionnaireTemplateStatus templateStatus;
    private List<QuestionnaireSectionResponseDTO> sections;
    private QuestionnaireTemplateType riskAssociatedEntity;
    private Map<String, QuestionType>  questionTypeMap;

    public QuestionnaireTemplateResponseDTO(Long id, String name, String description, QuestionnaireTemplateType templateType, boolean isDefaultAssetTemplate, QuestionnaireTemplateStatus templateStatus,QuestionnaireTemplateType riskAssociatedEntity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.templateType = templateType;
        this.isDefaultAssetTemplate = isDefaultAssetTemplate;
        this.templateStatus = templateStatus;
        this.riskAssociatedEntity=riskAssociatedEntity;
    }
}
