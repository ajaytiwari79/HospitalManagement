package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireTemplateResponseDTO {


    private BigInteger id;
    private String name;
    private String description;
    private QuestionnaireTemplateType templateType;
    private boolean defaultAssetTemplate;
    private AssetType assetType;
    private AssetType assetSubType;
    private QuestionnaireTemplateStatus templateStatus;
    private List<QuestionnaireSectionResponseDTO> sections;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public AssetType getAssetSubType() { return assetSubType; }

    public void setAssetSubType(AssetType assetSubType) { this.assetSubType = assetSubType; }

    public boolean isDefaultAssetTemplate() { return defaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.defaultAssetTemplate = defaultAssetTemplate; }

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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public List<QuestionnaireSectionResponseDTO> getSections() {
        return sections;
    }

    public void setSections(List<QuestionnaireSectionResponseDTO> sections) {
        this.sections = sections;
    }

    public QuestionnaireTemplateResponseDTO(BigInteger id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }

    public QuestionnaireTemplateResponseDTO(BigInteger id) {
        this.id = id;
    }

    public QuestionnaireTemplateResponseDTO() {
    }
}
