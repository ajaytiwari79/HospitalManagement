package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.QuestionnaireAssetTypeDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import org.javers.core.metamodel.annotation.Value;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireTemplateResponseDTO {


    private Long id;
    private String name;
    private String description;
    private QuestionnaireTemplateType templateType;
    private boolean isDefaultAssetTemplate;
    private QuestionnaireAssetTypeDTO assetType;
    private QuestionnaireAssetTypeDTO assetSubType;
    private QuestionnaireTemplateStatus templateStatus;
    private List<QuestionnaireSectionResponseDTO> sections;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public QuestionnaireAssetTypeDTO getAssetSubType() { return assetSubType; }

    public void setAssetSubType(QuestionnaireAssetTypeDTO assetSubType) { this.assetSubType = assetSubType; }

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
}
