package com.kairos.persistence.model.organization;

import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.user.organization.CompanyType;
import com.kairos.user.organization.CompanyUnitType;

import java.time.ZoneId;
import java.util.List;

public class OrganizationBuilder {
    private String name;
    private Long id;
    private List<Group> groupList;
    private List<Organization> children;
    private boolean parentOrganization;
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
    private OrganizationType organizationType;
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
    private UnitType unitType;

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
        this.parentOrganization = isParentOrganization;
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

    public OrganizationBuilder setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
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

    public OrganizationBuilder setUnitType(UnitType unitType) {
        this.unitType = unitType;
        return this;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public List<Organization> getChildren() {
        return children;
    }

    public boolean isParentOrganization() {
        return parentOrganization;
    }


    public Country getCountry() {
        return country;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public boolean isBoardingCompleted() {
        return boardingCompleted;
    }

    public String getKairosId() {
        return kairosId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrekairos() {
        return isPrekairos;
    }

    public void setPrekairos(boolean prekairos) {
        isPrekairos = prekairos;
    }

    public String getDesiredUrl() {
        return desiredUrl;
    }

    public String getShortCompanyName() {
        return shortCompanyName;
    }

    public Integer getKairosCompanyId() {
        return kairosCompanyId;
    }

    public String getVatId() {
        return vatId;
    }

    public List<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public List<OrganizationType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public CompanyUnitType getCompanyUnitType() {
        return companyUnitType;
    }

    public CompanyCategory getCompanyCategory() {
        return companyCategory;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public OrganizationSetting getOrganizationSetting() {
        return organizationSetting;
    }

    public OrganizationLevel getOrganizationLevel() {
        return organizationLevel;
    }

    public String getEmail() {
        return email;
    }

    public ContactDetail getContact() {
        return contact;
    }

    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public String getChildLevel() {
        return childLevel;
    }

    public UnitType getUnitType() {
        return unitType;
    }


    public Organization createOrganization() {
        return new  Organization( id,name, description,isPrekairos, desiredUrl, shortCompanyName,kairosCompanyId, companyType,
                vatId, businessTypes,organizationType, organizationSubTypes,  companyUnitType, companyCategory, timeZone,childLevel,
                parentOrganization, country,accountType,boardingCompleted,kairosId,groupList,children,unitType);
    }

}