package com.kairos.persistence.model.agreement_template;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.dto.gdpr.agreement_template.CoverPageVO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document
public class PolicyAgreementTemplate extends MongoBaseEntity {

    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private Long countryId;
    private List<AccountTypeVO> accountTypes;
    private List<BigInteger> agreementSections=new ArrayList<>();
    private List<OrganizationType> organizationTypes;
    private List<OrganizationSubType> organizationSubTypes;
    private List<ServiceCategory> organizationServices;
    private List<SubServiceCategory> organizationSubServices;
    private BigInteger templateType;
    private boolean coverPageAdded;
    private boolean includeContentPage;
    private boolean signatureComponentAdded;
    private boolean signatureComponentLeftAlign;
    private boolean signatureComponentRightAlign;
    private String  signatureHtml;
    private CoverPageVO coverPageData;


    public PolicyAgreementTemplate(String name, String description, Long countryId, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
    }

    public PolicyAgreementTemplate(@NotBlank(message = "Name cannot be empty") String name, @NotBlank(message = "Description cannot be empty") String description, BigInteger templateType) {
        this.name = name;
        this.description = description;
        this.templateType = templateType;
    }

    public boolean isIncludeContentPage() { return includeContentPage; }

    public void setIncludeContentPage(boolean includeContentPage) { this.includeContentPage = includeContentPage; }

    public PolicyAgreementTemplate() {
    }

    public boolean isCoverPageAdded() { return coverPageAdded; }

    public void setCoverPageAdded(boolean coverPageAdded) { this.coverPageAdded = coverPageAdded; }

    public CoverPageVO getCoverPageData() { return coverPageData; }

    public void setCoverPageData(CoverPageVO coverPageData) { this.coverPageData = coverPageData; }

    public BigInteger getTemplateType() { return templateType; }

    public PolicyAgreementTemplate setTemplateType(BigInteger templateType) { this.templateType = templateType; return this; }

    public String getName() {
        return name;
    }

    public PolicyAgreementTemplate setName(String name) { this.name = name;return this; }

    public String getDescription() {
        return description;
    }

    public PolicyAgreementTemplate setDescription(String description) { this.description = description;return this; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<BigInteger> agreementSections) { this.agreementSections = agreementSections; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public PolicyAgreementTemplate setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes;return this; }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public PolicyAgreementTemplate setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; return this;}

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public PolicyAgreementTemplate setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public PolicyAgreementTemplate setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public List<AccountTypeVO> getAccountTypes() { return accountTypes; }

    public PolicyAgreementTemplate setAccountTypes(List<AccountTypeVO> accountTypes) { this.accountTypes = accountTypes;return this; }

    public boolean isSignatureComponentAdded() { return signatureComponentAdded; }

    public void setSignatureComponentAdded(boolean signatureComponentAdded) { this.signatureComponentAdded = signatureComponentAdded; }

    public String getSignatureHtml() { return signatureHtml; }

    public void setSignatureHtml(String signatureHtml) { this.signatureHtml = signatureHtml; }

    public boolean isSignatureComponentLeftAlign() { return signatureComponentLeftAlign; }

    public void setSignatureComponentLeftAlign(boolean signatureComponentLeftAlign) { this.signatureComponentLeftAlign = signatureComponentLeftAlign; }

    public boolean isSignatureComponentRightAlign() { return signatureComponentRightAlign; }

    public void setSignatureComponentRightAlign(boolean signatureComponentRightAlign) { this.signatureComponentRightAlign = signatureComponentRightAlign; }
}
