package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "questionnaire_template")
public class QuestionnaireTemplate extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotBlank(message = "Template type cannot be empty ")
    private QuestionnaireTemplateType templateType;
    private BigInteger assetType;
    private BigInteger assetSubType;
    private Long countryId;
    private boolean defaultAssetTemplate;

    private List<BigInteger> sections=new ArrayList<>();

    public QuestionnaireTemplate(String name, Long countryId, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
    }

    public QuestionnaireTemplate(@NotBlank(message = "Name can't be empty") String name, @NotBlank(message = "Description cannot be empty") String description) {
        this.name = name;
        this.description = description;
    }

    public BigInteger getAssetType() { return assetType; }

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<BigInteger> getSections() { return sections; }

    public void setSections(List<BigInteger> sections) { this.sections = sections; }

    public QuestionnaireTemplateType getTemplateType() { return templateType; }

    public void setTemplateType(QuestionnaireTemplateType templateType) {
        this.templateType = templateType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isDefaultAssetTemplate() { return defaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.defaultAssetTemplate = defaultAssetTemplate; }

    public BigInteger getAssetSubType() { return assetSubType; }

    public void setAssetSubType(BigInteger assetSubType) { this.assetSubType = assetSubType;    }



    public QuestionnaireTemplate() {
    }
}
