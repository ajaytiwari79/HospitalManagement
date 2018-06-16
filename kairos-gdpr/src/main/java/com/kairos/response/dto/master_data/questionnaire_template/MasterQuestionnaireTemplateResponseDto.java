package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.master_data_management.asset_management.AssetType;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateResponseDto {


    private BigInteger id;

    private String name;
    private String description;
    private String templateType;

    private AssetType assetType;

    private List<MasterQuestionnaireSectionResponseDto> sections;

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

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public List<MasterQuestionnaireSectionResponseDto> getSections() {
        return sections;
    }

    public void setSections(List<MasterQuestionnaireSectionResponseDto> sections) {
        this.sections = sections;
    }

    public MasterQuestionnaireTemplateResponseDto(BigInteger id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }

    public MasterQuestionnaireTemplateResponseDto(BigInteger id) {
        this.id = id;
    }

    public MasterQuestionnaireTemplateResponseDto() {
    }
}
