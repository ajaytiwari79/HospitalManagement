package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.model.master_data_management.asset_management.StorageType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateQueryResult {

    private BigInteger id;

    private String name;
    private String description;

    @NotNullOrEmpty(message = "questionnaire type cannot be empty or null")
    private String templateType;

    private StorageType assetType;

    private List<MasterQuestionnaireSection> sections=new ArrayList<>() ;

    private List<MasterQuestion> questions=new ArrayList<>();


    public List<MasterQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MasterQuestion> questions) {
        this.questions = questions;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public List<MasterQuestionnaireSection> getSections() {
        return sections;
    }

    public void setSections(List<MasterQuestionnaireSection> sections) {
        this.sections = sections;
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


    public StorageType getAssetType() {
        return assetType;
    }

    public void setAssetType(StorageType assetType) {
        this.assetType = assetType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
