package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionnaireTemplate extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @NotNull(message = "error.message.templateType.notNull")
    private QuestionnaireTemplateType templateType;
    @OneToOne
    private AssetType assetType;
    @OneToOne
    private AssetType subAssetType;
    private Long countryId;
    private boolean isDefaultAssetTemplate;
    private QuestionnaireTemplateStatus templateStatus;
    private QuestionnaireTemplateType riskAssociatedEntity;
    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_template_id" )
    private List<QuestionnaireSection> sections=new ArrayList<>();
    private Long organizationId;


    public QuestionnaireTemplate(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, Long countryId,@NotBlank(message = "error.message.description.notNull.orEmpty") String description, @NotNull(message = "error.message.templateType.notNull")QuestionnaireTemplateType templateType) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;
        this.templateType=templateType;
    }

    public QuestionnaireTemplate(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description, @NotNull(message = "error.message.templateType.notNull") QuestionnaireTemplateType templateType,QuestionnaireTemplateStatus templateStatus,Long organizationId) {
        this.name = name;
        this.description = description;
        this.templateStatus=templateStatus;
        this.templateType=templateType;
        this.organizationId=organizationId;
    }
    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

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
        return subAssetType;
    }

    public void setAssetSubType(AssetType subAssetType) {
        this.subAssetType = subAssetType;
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
        this.getSections().forEach(QuestionnaireSection::delete);
    }

}
