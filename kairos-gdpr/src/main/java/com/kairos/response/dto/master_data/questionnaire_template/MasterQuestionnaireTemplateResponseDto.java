package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateResponseDto {

    private BigInteger id;

    private String name;
    private String description;

    @NotNullOrEmpty(message = "questionnaire type cannot be empty or null")
    private String questionnaireType;

    private BigInteger assetType;

    private List<MasterQuestionnaireSectionResponseDto> questionSections;

    public List<MasterQuestionnaireSectionResponseDto> getQuestionSections() {
        return questionSections;
    }

    public void setQuestionSections(List<MasterQuestionnaireSectionResponseDto> questionSections) {
        this.questionSections = questionSections;
    }

    private Long countryId;

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

    public String getQuestionnaireType() {
        return questionnaireType;
    }

    public void setQuestionnaireType(String questionnaireType) {
        this.questionnaireType = questionnaireType;
    }

    public BigInteger getAssetType() {
        return assetType;
    }

    public void setAssetType(BigInteger assetType) {
        this.assetType = assetType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
