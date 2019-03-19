package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.QuestionnaireAssetTypeDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import java.util.List;

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


    public QuestionnaireAssetTypeDTO getSubAssetType() { return subAssetType; }

    public void setSubAssetType(QuestionnaireAssetTypeDTO subAssetType) { this.subAssetType = subAssetType; }

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) {
        this.riskAssociatedEntity = riskAssociatedEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public QuestionnaireAssetTypeDTO getAssetSubType() { return subAssetType; }

    public void setAssetSubType(QuestionnaireAssetTypeDTO subAssetType) { this.subAssetType = subAssetType; }

    public boolean isDefaultAssetTemplate() { return isDefaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.isDefaultAssetTemplate = defaultAssetTemplate; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestionnaireTemplateType getTemplateType() { return templateType; }

    public void setTemplateType(QuestionnaireTemplateType templateType) { this.templateType = templateType; }

    public QuestionnaireAssetTypeDTO getAssetType() {
        return assetType;
    }

    public void setAssetType(QuestionnaireAssetTypeDTO assetType) {
        this.assetType = assetType;
    }

    public List<QuestionnaireSectionResponseDTO> getSections() {
        return sections;
    }

    public void setSections(List<QuestionnaireSectionResponseDTO> sections) {
        this.sections = sections;
    }

    public QuestionnaireTemplateResponseDTO() {
    }

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
