package com.kairos.persistance.model.master_data.questionnaire_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "questionnaire_template")
public class MasterQuestionnaireTemplate extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    @Pattern(message = "Number and Special character are not allowed for Title",regexp ="^[a-zA-Z\\s]+$" )
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Template type cannot be empty ")
    private String templateType;

    private BigInteger assetType;

    private Long countryId;

    private List<BigInteger> sections=new ArrayList<>();

    public BigInteger getAssetType() {
        return assetType;
    }

    public void setAssetType(BigInteger assetType) {
        this.assetType = assetType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BigInteger> getSections() {
        return sections;
    }

    public void setSections(List<BigInteger> sections) {
        this.sections = sections;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public MasterQuestionnaireTemplate(String name, Long countryId, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
    }

    public MasterQuestionnaireTemplate() {
    }
}
