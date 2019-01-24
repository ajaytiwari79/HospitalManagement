package com.kairos.dto.gdpr.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireTemplateDTO {

    private BigInteger id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @NotNull(message = "Template type cannot be empty ")
    private QuestionnaireTemplateType templateType;
    private BigInteger assetType;
    private BigInteger assetSubType;
    private boolean defaultAssetTemplate;
    private QuestionnaireTemplateStatus templateStatus;
    private QuestionnaireTemplateType riskAssociatedEntity;
    @Valid
    private List<QuestionnaireSectionDTO> sections=new ArrayList<>();

    public BigInteger getAssetSubType() { return assetSubType; }

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) { this.riskAssociatedEntity = riskAssociatedEntity; }

    public void setAssetSubType(BigInteger assetSubType) { this.assetSubType = assetSubType; }

    public boolean isDefaultAssetTemplate() { return defaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.defaultAssetTemplate = defaultAssetTemplate; }

    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public String getName() {
        return name.trim();
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

    public QuestionnaireTemplateType getTemplateType() { return templateType;}

    public void setTemplateType(QuestionnaireTemplateType templateType) { this.templateType = templateType; }

    public BigInteger getAssetType() { return assetType; }

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }

    public BigInteger getId() { return id; }

    public QuestionnaireTemplateDTO setId(BigInteger id) { this.id = id;return this;}

    public List getSections() { return sections; }

    public void setSections(List sections) { this.sections = sections; }

    public QuestionnaireTemplateDTO() {
    }
}
