package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class QuestionnaireTemplateDeprecated {

    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotBlank(message = "Template type cannot be empty ")
    private QuestionnaireTemplateType templateType;
    private BigInteger assetTypeId;
    private BigInteger assetSubTypeId;
    private Long countryId;
    private boolean defaultAssetTemplate;
    private QuestionnaireTemplateStatus templateStatus;
    private QuestionnaireTemplateType riskAssociatedEntity;
    private List<BigInteger> sections=new ArrayList<>();

    public QuestionnaireTemplateDeprecated(String name, Long countryId, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
    }

    public QuestionnaireTemplateDeprecated(@NotBlank(message = "Name can't be empty") String name, @NotBlank(message = "Description cannot be empty") String description, QuestionnaireTemplateStatus templateStatus) {
        this.name = name;
        this.description = description;
        this.templateStatus=templateStatus;
    }

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) { this.riskAssociatedEntity = riskAssociatedEntity; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name.trim(); }
    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public List<BigInteger> getSections() { return sections; }

    public void setSections(List<BigInteger> sections) { this.sections = sections; }

    public QuestionnaireTemplateType getTemplateType() { return templateType; }

    public void setTemplateType(QuestionnaireTemplateType templateType) { this.templateType = templateType; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isDefaultAssetTemplate() { return defaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.defaultAssetTemplate = defaultAssetTemplate; }

    public BigInteger getAssetTypeId() { return assetTypeId; }

    public void setAssetTypeId(BigInteger assetTypeId) { this.assetTypeId = assetTypeId; }

    public BigInteger getAssetSubTypeId() { return assetSubTypeId; }

    public void setAssetSubTypeId(BigInteger assetSubTypeId) { this.assetSubTypeId = assetSubTypeId; }

    public QuestionnaireTemplateDeprecated() {
    }
}
