package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionnaireTemplateMD extends BaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Template type cannot be empty ")
    private QuestionnaireTemplateType templateType;

    @OneToOne
    private AssetTypeMD assetType;

    @OneToOne
    private AssetTypeMD assetSubType;

    private Long countryId;

    private boolean isDefaultAssetTemplate;

    private QuestionnaireTemplateStatus templateStatus;

    private QuestionnaireTemplateType riskAssociatedEntity;

    @OneToMany(cascade = CascadeType.ALL)
    private List<QuestionnaireSectionMD> sections=new ArrayList<>();

    public QuestionnaireTemplateMD(String name, Long countryId, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
    }

    public QuestionnaireTemplateMD(@NotBlank(message = "Name can't be empty") String name, @NotBlank(message = "Description cannot be empty") String description, QuestionnaireTemplateStatus templateStatus) {
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

    public AssetTypeMD getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetTypeMD assetType) {
        this.assetType = assetType;
    }

    public AssetTypeMD getAssetSubType() {
        return assetSubType;
    }

    public void setAssetSubType(AssetTypeMD assetSubType) {
        this.assetSubType = assetSubType;
    }

    public List<QuestionnaireSectionMD> getSections() {
        return sections;
    }

    public void setSections(List<QuestionnaireSectionMD> sections) {
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

    public QuestionnaireTemplateMD() {
    }

    @Override
    public void delete() {
        super.delete();
        this.getSections().forEach( section -> {
            section.delete();
        });
    }
}
