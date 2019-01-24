package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateTypeMD;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PolicyAgreementTemplateMD extends BaseEntity {

    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private Long countryId;

    @ElementCollection
    private List<AccountType> accountTypes;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderColumn
    private List<AgreementSectionMD> agreementSections=new ArrayList<>();

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @OneToOne
    private TemplateTypeMD templateType;
    private boolean coverPageAdded;
    private boolean includeContentPage;
    private boolean signatureComponentAdded;
    private boolean signatureComponentLeftAlign;
    private boolean signatureComponentRightAlign;
    private String  signatureHtml;

    @Embedded
    private CoverPage coverPageData = new CoverPage();
    /*@Transient
    private ClauseTag defaultClauseTag;*/


    public PolicyAgreementTemplateMD(String name, String description, Long countryId, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
    }

    public PolicyAgreementTemplateMD(@NotBlank(message = "Name cannot be empty") String name, @NotBlank(message = "Description cannot be empty") String description, TemplateTypeMD templateType) {
        this.name = name;
        this.description = description;
        this.templateType = templateType;
    }

    public boolean isIncludeContentPage() { return includeContentPage; }

    public void setIncludeContentPage(boolean includeContentPage) { this.includeContentPage = includeContentPage; }

    public PolicyAgreementTemplateMD() {
    }

    public boolean isCoverPageAdded() { return coverPageAdded; }

    public void setCoverPageAdded(boolean coverPageAdded) { this.coverPageAdded = coverPageAdded; }

    public String getName() {
        return name;
    }

    public PolicyAgreementTemplateMD setName(String name) { this.name = name;return this; }

    public String getDescription() {
        return description;
    }

    public PolicyAgreementTemplateMD setDescription(String description) { this.description = description;return this; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public PolicyAgreementTemplateMD setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; return this;}

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public PolicyAgreementTemplateMD setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public PolicyAgreementTemplateMD setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public List<AccountType> getAccountTypes() { return accountTypes; }

    public PolicyAgreementTemplateMD setAccountTypes(List<AccountType> accountTypes) { this.accountTypes = accountTypes;return this; }

    public boolean isSignatureComponentAdded() { return signatureComponentAdded; }

    public void setSignatureComponentAdded(boolean signatureComponentAdded) { this.signatureComponentAdded = signatureComponentAdded; }

    public String getSignatureHtml() { return signatureHtml; }

    public void setSignatureHtml(String signatureHtml) { this.signatureHtml = signatureHtml; }

    public boolean isSignatureComponentLeftAlign() { return signatureComponentLeftAlign; }

    public void setSignatureComponentLeftAlign(boolean signatureComponentLeftAlign) { this.signatureComponentLeftAlign = signatureComponentLeftAlign; }

    public boolean isSignatureComponentRightAlign() { return signatureComponentRightAlign; }

    public void setSignatureComponentRightAlign(boolean signatureComponentRightAlign) { this.signatureComponentRightAlign = signatureComponentRightAlign; }

   /* public ClauseTag getDefaultClauseTag() { return defaultClauseTag; }

    public void setDefaultClauseTag(ClauseTag defaultClauseTag) { this.defaultClauseTag = defaultClauseTag; }
*/
    public List<AgreementSectionMD> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<AgreementSectionMD> agreementSections) {
        this.agreementSections = agreementSections;
    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public TemplateTypeMD getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateTypeMD templateType) {
        this.templateType = templateType;
    }

    public CoverPage getCoverPageData() {
        return coverPageData;
    }

    public void setCoverPageData(CoverPage coverPageData) {
        this.coverPageData = coverPageData;
    }

    @Override
    public void delete() {
        super.delete();
        this.getAgreementSections().forEach( agreementSection -> {
            agreementSection.delete();
        });
    }
}
