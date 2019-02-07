package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionnaireTemplate extends BaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotNull(message = "Template type cannot be empty ")
    private QuestionnaireTemplateType templateType;
    @OneToOne
    private AssetType assetType;
    @OneToOne
    private AssetType assetSubType;
    private Long countryId;
    private boolean isDefaultAssetTemplate;
    private QuestionnaireTemplateStatus templateStatus;
    private QuestionnaireTemplateType riskAssociatedEntity;
    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name = "questionnaire_template_id")
    private List<QuestionnaireSection> sections=new ArrayList<>();



    public QuestionnaireTemplate(String name, Long countryId, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
    }

    public QuestionnaireTemplate(@NotBlank(message = "Name can't be empty") String name, @NotBlank(message = "Description cannot be empty") String description, QuestionnaireTemplateStatus templateStatus) {
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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public AssetType getAssetSubType() {
        return assetSubType;
    }

    public void setAssetSubType(AssetType assetSubType) {
        this.assetSubType = assetSubType;
    }

    public List<QuestionnaireSection> getSections() {
        return sections;
    }

    public void setSections(List<QuestionnaireSection> sections) {
        this.sections = sections;
    }

    public QuestionnaireTemplateType getTemplateType() { return templateType; }

    public void setTemplateType(QuestionnaireTemplateType templateType) { this.templateType = templateType; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isDefaultAssetTemplate() { return isDefaultAssetTemplate; }

    public void setDefaultAssetTemplate(boolean defaultAssetTemplate) { this.isDefaultAssetTemplate = defaultAssetTemplate; }

    public QuestionnaireTemplate() {
    }

    @Override
    public void delete() {
        super.delete();
        this.getSections().forEach( section -> {
            section.delete();
        });
    }
}
