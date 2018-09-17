package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateResponseDTO {


    private BigInteger id;

    private String name;
    private String description;
    private QuestionnaireTemplateType templateType;

    private AssetType assetType;

    private List<MasterQuestionnaireSectionResponseDTO> sections;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public List<MasterQuestionnaireSectionResponseDTO> getSections() {
        return sections;
    }

    public void setSections(List<MasterQuestionnaireSectionResponseDTO> sections) {
        this.sections = sections;
    }

    public MasterQuestionnaireTemplateResponseDTO(BigInteger id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }

    public MasterQuestionnaireTemplateResponseDTO(BigInteger id) {
        this.id = id;
    }

    public MasterQuestionnaireTemplateResponseDTO() {
    }
}
