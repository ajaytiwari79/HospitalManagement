package com.kairos.persistence.model.organization;

import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.user.organization.CompanyType;
import com.kairos.user.organization.CompanyUnitType;

import java.time.ZoneId;
import java.util.List;

public class OrganizationBuilder {
    private String name;
    private List<Group> groupList;
    private List<Organization> children;
    private boolean isParentOrganization;
    private Country country;
    private AccountType accountType;
    private CompanyType companyType;
    private boolean boardingCompleted;
    private String kairosId;
    private String description;
    private boolean isPrekairos;
    private String desiredUrl;
    private String shortCompanyName;
    private Integer kairosCompanyId;
    private String vatId;
    private List<BusinessType> businessTypes;
    private List<OrganizationType> organizationTypes;
    private List<OrganizationType> organizationSubTypes;
    private CompanyUnitType companyUnitType;
    private CompanyCategory companyCategory;
    private ZoneId timeZone;
    private OrganizationSetting organizationSetting;
    private OrganizationLevel organizationLevel;
    private String email;
    private ContactDetail contact;
    private ContactAddress contactAddress;
    private String childLevel;

    public OrganizationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public OrganizationBuilder setGroupList(List<Group> groupList) {
        this.groupList = groupList;
        return this;
    }

    public OrganizationBuilder setChildren(List<Organization> children) {
        this.children = children;
        return this;
    }

    public OrganizationBuilder setIsParentOrganization(boolean isParentOrganization) {
        this.isParentOrganization = isParentOrganization;
        return this;
    }

    public OrganizationBuilder setCountry(Country country) {
        this.country = country;
        return this;
    }

    public OrganizationBuilder setAccountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public OrganizationBuilder setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
        return this;
    }

    public OrganizationBuilder setBoardingCompleted(boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
        return this;
    }

    public OrganizationBuilder setKairosId(String kairosId) {
        this.kairosId = kairosId;
        return this;
    }

    public OrganizationBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public OrganizationBuilder setIsPrekairos(boolean isPrekairos) {
        this.isPrekairos = isPrekairos;
        return this;
    }

    public OrganizationBuilder setDesiredUrl(String desiredUrl) {
        this.desiredUrl = desiredUrl;
        return this;
    }

    public OrganizationBuilder setShortCompanyName(String shortCompanyName) {
        this.shortCompanyName = shortCompanyName;
        return this;
    }

    public OrganizationBuilder setKairosCompanyId(Integer kairosCompanyId) {
        this.kairosCompanyId = kairosCompanyId;
        return this;
    }

    public OrganizationBuilder setVatId(String vatId) {
        this.vatId = vatId;
        return this;
    }

    public OrganizationBuilder setBusinessTypes(List<BusinessType> businessTypes) {
        this.businessTypes = businessTypes;
        return this;
    }

    public OrganizationBuilder setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
        return this;
    }

    public OrganizationBuilder setOrganizationSubTypes(List<OrganizationType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
        return this;
    }

    public OrganizationBuilder setCompanyUnitType(CompanyUnitType companyUnitType) {
        this.companyUnitType = companyUnitType;
        return this;
    }

    public OrganizationBuilder setCompanyCategory(CompanyCategory companyCategory) {
        this.companyCategory = companyCategory;
        return this;
    }

    public OrganizationBuilder setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public OrganizationBuilder setOrganizationSetting(OrganizationSetting organizationSetting) {
        this.organizationSetting = organizationSetting;
        return this;
    }

    public OrganizationBuilder setOrganizationLevel(OrganizationLevel organizationLevel) {
        this.organizationLevel = organizationLevel;
        return this;
    }

    public OrganizationBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public OrganizationBuilder setContact(ContactDetail contact) {
        this.contact = contact;
        return this;
    }

    public OrganizationBuilder setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
        return this;
    }

    public OrganizationBuilder setChildLevel(String childLevel) {
        this.childLevel = childLevel;
        return this;
    }

    public Organization createOrganization() {
        return new  Organization( name, description,isPrekairos, desiredUrl, shortCompanyName,kairosCompanyId, companyType,
                vatId, businessTypes,organizationTypes, organizationSubTypes,  companyUnitType, companyCategory, timeZone);
    }

}